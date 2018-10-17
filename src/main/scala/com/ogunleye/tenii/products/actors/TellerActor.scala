package com.ogunleye.tenii.products.actors

import java.time.LocalDate

import akka.actor.{Actor, ActorSystem}
import com.ogunleye.tenii.products.db.TellerTransactionConnection
import com.ogunleye.tenii.products.external.HttpTransfers
import com.ogunleye.tenii.products.model.api.teller.{TellerTeniiPaymentsResponse, _}
import com.ogunleye.tenii.products.model.db.TellerDebitTransaction
import com.ogunleye.tenii.products.model.implicits.TransactionImplicit
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class TellerActor extends Actor with LazyLogging with TellerEndpoints with TransactionImplicit with PaymentsEndpoints {

  implicit val system: ActorSystem = context.system
  val http = new HttpTransfers()
  implicit val timeout: FiniteDuration = 10.seconds
  val connection = new TellerTransactionConnection

  override def receive: Receive = {
    case request: TellerAccountsRequest =>
      //TODO logic to create tenii pot for new user
      //Send teller Id and limit
      val senderRef = sender()
      http.endpointGet[List[TellerAccountResponse]](s"$apiHost$accounts", ("Authorization", s"Bearer ${request.id}")).onComplete {
        case Success(resp) => senderRef ! resp
        case Failure(t) => senderRef ! t
      }
    case request: TellerTransactionsRequest =>
      val senderRef = sender()
      http.endpointGet[List[TellerTransaction]](s"$apiHost$accounts${request.accountId}$transactions", ("Authorization", s"Bearer ${request.id}")).onComplete {
        case Success(resp) => senderRef ! resp
          val today = LocalDate.now()
          val month = today.getMonthValue
          val formattedMonth = if(month < 10) "0" + month else month.toString
          val date = Integer.parseInt(s"${today.getYear}$formattedMonth${today.getDayOfMonth}")
          val transactions = resp.takeWhile(i => Integer.parseInt(i.date.replace("-","")) >= date)
          if(transactions.nonEmpty) {
            self ! (transactions, request.accountId)
            logger.info(s"Sending ${transactions.size} transactions for potential tenii payments")
          }
          else {
            logger.info(s"No transactions to process")
          }
        case Failure(t) => senderRef ! t
      }
    case request: (List[TellerTransaction], String) =>
      val lastTransaction = request._1.head
      val candidateTransactions = request._1.filter(i => Double.unbox(i.amount) < 0)
      if(candidateTransactions.nonEmpty) {
        Future {
          connection.findByTellerAccount(request._2)
        } onComplete {
          case Success(opt) => opt match {
            case Some(tran) => if(tran.tellerTransactionId == lastTransaction.id) {
              logger.info(s"Nothing needs to be done, last transaction already accounted for")
            }
            else {
              val newTrans = candidateTransactions.takeWhile(_.id != tran.tellerTransactionId)
              sendTransactionsToPaymentsAPI(candidateTransactions, request._2)
              saveTran(newTrans.head, request._2, tran)
            }
            case None => sendTransactionsToPaymentsAPI(candidateTransactions, request._2)
              saveTran(lastTransaction, request._2)
          }
          case Failure(t) => logger.error(s"Failed to find transactions, manually update db and send tenii transactions to payments service. $candidateTransactions", t)
        }
      }
      else {
        logger.debug(s"No debit transactions received: $request")
      }
    case other => logger.error(s"Unknown message received $other")
  }

  private def sendTransactionsToPaymentsAPI(transactions: List[TellerTransaction], accountId: String) : Unit = {

    transactions.foreach { trans =>
      Future {
        http.endpoint[TellerTeniiPotCreditRequest, TellerTeniiPaymentsResponse](s"$paymentsApiHost$updatePot", toTellerTeniiPotCreditRequest(trans, accountId)) onComplete {
          case Success(_) => logger.info(s"Successfully sent payment for Tenii round up $trans")
          case Failure(exception) => logger.error(s"Error thrown when attemoting to send payment for round up; $trans", exception)
        }
      }
    }
    ()
  }

  private def saveTran(trans: TellerTransaction, account: String, oldTran: TellerDebitTransaction*) : Unit = {
    val tran =  if(oldTran.nonEmpty)
      toTellerDebitTransaction(trans, account)
    else
      toTellerDebitTransaction(trans, account).copy(id = oldTran.head.id)

    Future {
      connection.save(tran)
    } onComplete {
      case Success(_) => logger.info(s"Saved last transaction")
      case Failure(t) => logger.error(s"Error saving latest transaction, check: $tran", t)
    }
  }

}

trait PaymentsEndpoints {

  val paymentsApiHost = "https://tenii-payments-api.heroku.com/"
  val updatePot = "updatePot/"

  implicit def onSuccessDecodingError[TellerTeniiPaymentsResponse](decodingError: io.circe.Error): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding trains upstream response: $decodingError")
  implicit def onErrorDecodingError[TellerTeniiPaymentsResponse](decodingError: String): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding upstream error response: $decodingError")
}

trait TellerEndpoints {

  val apiHost = "https://api.teller.io/"
  val appId = "application_id="
  val accounts = "accounts/"
  val transactions = "/transactions"
  val permissions = "&permissions=balance:true,full_account_number:true,transaction_history:true"

}