package com.ogunleye.tenii.products.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.ogunleye.tenii.products.db.TransactionConnection
import com.ogunleye.tenii.products.external.{HttpTransfers, PaymentEndpoints}
import com.ogunleye.tenii.products.helpers.{NumberHelper, TransactionHelper}
import com.ogunleye.tenii.products.model.api._
import com.ogunleye.tenii.products.model.{Roar, RoarType}
import com.ogunleye.tenii.products.model.db.{BankAccount, DaysTransactions => DBTransaction}
import com.ogunleye.tenii.products.model.implicits.TransactionImplicit
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class TransactionActor extends Actor with LazyLogging with TransactionImplicit with PaymentEndpoints {

  val connection = new TransactionConnection
  val bankAccountActor: ActorRef = context.actorOf(Props[BankAccountActor])
  val mortgageActor: ActorRef = context.actorOf(Props[MortgageActor])
  implicit val system = context.system
  val http = new HttpTransfers()

  override def receive: Receive = {
    case trans: Transaction =>
      val senderRef = sender()
      implicit val timeout: Timeout = Timeout(10.seconds)
      val accountOpt = bankAccountActor ? trans
      val transactionOpt = Future { connection.findByTeniiId(trans.teniiId) }
      val result = for {
        account <- accountOpt
        transaction <- transactionOpt
      } yield (account, transaction)
      result.onComplete {
        case Success(accOpt : (Any, Option[DBTransaction])) => val acct = accOpt._1.asInstanceOf[Option[BankAccount]]
          val date = TransactionHelper.dateToNumber(trans.date)
          val roundedAmount = TransactionHelper.applyRoundingForRoarType(RoarType(Roar.BALANCED), trans)
          (acct,accOpt._2) match {
          case (Some(_), Some(dbTran)) => val dbDate = TransactionHelper.dateToNumber(dbTran.date)
            if(dbDate < date || (dbDate == date && dbTran.transactionIds.contains(trans.transactionId))) {
              updateTrans(trans, dbTran, dbDate < date)
              senderRef ! ProcessTransactionResponse(trans.transactionId, None)
              logger.debug(s"Processed transaction: ${trans.transactionId}, sent to api for processing")
              sendToPayment(trans.teniiId, roundedAmount)
            }
            else {
              logger.debug(s"Received old transaction: $trans, db transaction: $dbTran will not send to api for processing")
              senderRef ! ProcessTransactionResponse(trans.transactionId, None)
            }
          case (Some(_), None) =>
            logger.debug(s"Processed transaction: ${trans.transactionId}, sent to api for processing")
            saveTrans(trans)
            senderRef ! ProcessTransactionResponse(trans.transactionId, None)
            sendToPayment(trans.teniiId, roundedAmount)
          case (None, _) => senderRef ! ProcessTransactionResponse(trans.transactionId, Some("No account found for transaction"))
            logger.error(s"No account found for transaction: $trans")
        }
        case Failure(t) => senderRef ! ProcessTransactionResponse(trans.transactionId, Some("Failed to process transaction"))
          logger.error(s"Failed to process transaction: $trans, due to error", t)
      }

    case request: GetTransactionRequest =>
      val senderRef = sender()
      Future {
        connection.findByTeniiId(request.teniiId)
      } onComplete {
        case Success(transOpt) => transOpt match {
          case Some(tran) => logger.info(s"Response $tran")
            senderRef ! GetTransactionResponse(tran.transactionIds, tran.teniiId)
          case None => senderRef ! GetTransactionResponse(Nil, request.teniiId)
        }
        case Failure(t) => senderRef ! GetTransactionErrorResponse(s"Error thrown when looking up transaction")
          logger.error(s"Error thrown when looking up transaction", t)
      }

    case other => logger.error(s"Unknown message received: $other")
//
//    case resp: BankTransactionResponse =>
//      resp.account match {
//        case Some(_) =>
//          updateBankBalance(resp.transaction, resp.account.get)
//          //TODO load roar type for user
//          if (TransactionHelper.checkIfRoarTypeAllowsRounding(RoarType(Roar.STRIPES), resp)) {
//            val account = resp.account.get
//            //TODO Check item to be paid off, not always mortgage
//            //TODO Send to actor with an ask
//            mortgageActor ! resp.copy(account = Some(account.copy(balance = roundTo2DPAsDouble(account.balance + resp.transaction.amount))))
//          } else {
//            resp.ref ! AddTransactionResponse(resp.transaction.userId, None)
//          }
//        case None => resp.ref ! AddTransactionResponse(resp.transaction.userId, Some("No account found"))
//      }
//
//    case resp: MortgageBankTransactionResponse =>
//      resp.mortgage match {
//        case Some(mortgage) =>
//          saveTrans(resp.transaction) onComplete {
//            case Success(_) =>
//              resp.ref ! AddTransactionResponse(resp.transaction.userId)
//              val intAmount = math.ceil(math.abs(resp.transaction.amount))
//              val extra = roundTo2DPAsDouble(intAmount + resp.transaction.amount)
//              val teniiTransaction = Transaction(resp.transaction.userId, "Tenii", "Debt-Sort-Code", "Debt-Acc-Num", resp.transaction.date, -extra)
//              mortgageActor ! mortgage.copy(balance = roundTo2DPAsDouble(mortgage.balance + teniiTransaction.amount))
//              saveTrans(teniiTransaction) onComplete {
//                case Success(_) =>
//                  logger.info(s"Tenii transaction $teniiTransaction for transaction ${resp.transaction}")
//                  updateBankBalance(teniiTransaction, resp.account.get)
//                case Failure(t) => logger.error(s"Error saving tenii transaction for user: ${resp.transaction}", t)
//              }
//            case Failure(t) =>
//              logger.error(s"Error saving transaction for user: ${resp.transaction.userId}", t)
//              resp.ref ! AddTransactionResponse(resp.transaction.userId, Some(t.getMessage))
//          }
//        case None => resp.ref ! AddTransactionResponse(resp.transaction.userId, Some("No mortgage found to update"))
//      }
  }

  def sendToPayment(teniiId: String, amount: Double) = {
    implicit val finiteDuration = 20.seconds
    http.endpoint[TeniiPotCreditRequest, TeniiPotCreditResponse](s"$paymentsApiHost$updatePot", toPotCreditRequest(teniiId, amount)) onComplete {
      case Success(response) if response.cause.isEmpty => logger.info(s"Processed pot credit for user $teniiId")
      case Success(response) if response.cause.nonEmpty => logger.error(s"Processed pot credit for user $teniiId due to ${response.cause.get}")
      case Failure(t) => logger.error(s"Failed to process pot credit", t)
    }
  }

  def saveTrans(trans: Transaction) = Future { connection.save(trans) }

  def updateTrans(trans: Transaction, dbTrans: DBTransaction, overwrite: Boolean) = Future {
    val toSave =
      if(overwrite)
        dbTrans.copy(transactionIds = List(trans.transactionId), amount = trans.amount, date = trans.date)
      else
        dbTrans.copy(transactionIds = dbTrans.transactionIds.::(trans.transactionId), amount = trans.amount)
    connection.save(toSave)
  }
//
//  def updateBankBalance(trans: Transaction, bankAccount: BankAccount): Unit = bankAccountActor ! bankAccount.copy(balance = roundTo2DPAsDouble(bankAccount.balance + trans.amount))
}
