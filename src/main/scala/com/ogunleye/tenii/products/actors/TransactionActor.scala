package com.ogunleye.tenii.products.actors

import akka.actor.{ Actor, ActorRef, Props }
import com.ogunleye.tenii.products.db.TransactionConnection
import com.ogunleye.tenii.products.helpers.{ NumberHelper, TransactionHelper }
import com.ogunleye.tenii.products.model.{ Roar, RoarType }
import com.ogunleye.tenii.products.model.api.temp.{ AddTransactionResponse, Transaction }
import com.ogunleye.tenii.products.model.db.BankAccount
import com.ogunleye.tenii.products.model.domain.{ BankTransaction, BankTransactionResponse, MortgageBankTransactionResponse }
import com.ogunleye.tenii.products.model.implicits.TransactionImplicit
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

class TransactionActor extends Actor with LazyLogging with TransactionImplicit with NumberHelper {

  val connection = new TransactionConnection
  val bankAccountActor: ActorRef = context.actorOf(Props[BankAccountActor])
  val mortgageActor: ActorRef = context.actorOf(Props[MortgageActor])

  override def receive: Receive = {
    case trans: Transaction => bankAccountActor ! BankTransaction(sender(), trans)

    case resp: BankTransactionResponse =>
      resp.account match {
        case Some(_) =>
          updateBankBalance(resp.transaction, resp.account.get)
          //TODO load roar type for user
          if (TransactionHelper.checkIfRoarTypeAllowsRounding(RoarType(Roar.STRIPES), resp)) {
            val account = resp.account.get
            //TODO Check item to be paid off, not always mortgage
            //TODO Send to actor with an ask
            mortgageActor ! resp.copy(account = Some(account.copy(balance = roundTo2DPAsDouble(account.balance + resp.transaction.amount))))
          } else {
            resp.ref ! AddTransactionResponse(resp.transaction.userId, None)
          }
        case None => resp.ref ! AddTransactionResponse(resp.transaction.userId, Some("No account found"))
      }

    case resp: MortgageBankTransactionResponse =>
      resp.mortgage match {
        case Some(mortgage) =>
          saveTrans(resp.transaction) onComplete {
            case Success(_) =>
              resp.ref ! AddTransactionResponse(resp.transaction.userId)
              val intAmount = math.ceil(math.abs(resp.transaction.amount))
              val extra = roundTo2DPAsDouble(intAmount + resp.transaction.amount)
              val teniiTransaction = Transaction(resp.transaction.userId, "Tenii", "Debt-Sort-Code", "Debt-Acc-Num", resp.transaction.date, -extra)
              mortgageActor ! mortgage.copy(balance = roundTo2DPAsDouble(mortgage.balance + teniiTransaction.amount))
              saveTrans(teniiTransaction) onComplete {
                case Success(_) =>
                  logger.info(s"Tenii transaction $teniiTransaction for transaction ${resp.transaction}")
                  updateBankBalance(teniiTransaction, resp.account.get)
                case Failure(t) => logger.error(s"Error saving tenii transaction for user: ${resp.transaction}", t)
              }
            case Failure(t) =>
              logger.error(s"Error saving transaction for user: ${resp.transaction.userId}", t)
              resp.ref ! AddTransactionResponse(resp.transaction.userId, Some(t.getMessage))
          }
        case None => resp.ref ! AddTransactionResponse(resp.transaction.userId, Some("No mortgage found to update"))
      }
  }

  def saveTrans(trans: Transaction) = Future { connection.save(trans) }

  def updateBankBalance(trans: Transaction, bankAccount: BankAccount): Unit = bankAccountActor ! bankAccount.copy(balance = roundTo2DPAsDouble(bankAccount.balance + trans.amount))
}
