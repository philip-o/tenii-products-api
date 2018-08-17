package com.ogunleye.tenii.products.actors

import akka.actor.Actor
import com.ogunleye.tenii.products.db.BankAccountConnection
import com.ogunleye.tenii.products.model.api.{ AddBankAccountResponse, BankAccount, GetBankAccountRequest, GetBankAccountResponse }
import com.ogunleye.tenii.products.model.db.{ BankAccount => DBBankAccount }
import com.ogunleye.tenii.products.model.domain.{ BankTransaction, BankTransactionResponse }
import com.ogunleye.tenii.products.model.implicits.AccountImplicit
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

class BankAccountActor extends Actor with LazyLogging with AccountImplicit {

  val connection = new BankAccountConnection

  override def receive: Receive = {
    case account: BankAccount =>
      val senderRef = sender()
      Future {
        connection.save(account)
      } onComplete {
        case Success(_) => senderRef ! AddBankAccountResponse(account.userId)
        case Failure(t) =>
          logger.error(s"Error saving mortgage for user: $account", t)
          senderRef ! AddBankAccountResponse(account.userId, Some(t.getMessage))
      }

    case request: GetBankAccountRequest =>
      val senderRef = sender()
      Future {
        connection.findByUserId(request.userId)
      } onComplete {
        case Success(accountOption) => accountOption match {
          case account :: _ => senderRef ! GetBankAccountResponse(Some(account), None)
          case Nil => senderRef ! GetBankAccountResponse(None, Some(s"No account exists for user ${request.userId}"))
        }
        case Failure(t) =>
          logger.error(s"Error retrieving account for user: ${request.userId}", t)
          senderRef ! GetBankAccountResponse(None, Some(s"Error retrieving account for user due to: ${t.getCause}"))
      }

    case request: BankTransaction =>
      val senderRef = sender()
      Future {
        connection.findByAccountNumberUserId(request.transaction.accountNumber, request.transaction.userId)
      } onComplete {
        case Success(optAccount) => optAccount match {
          case Some(account) => senderRef ! BankTransactionResponse(request.ref, request.transaction, Some(account))
          case None =>
            logger.error(s"Could not find account for ${request.transaction}")
            senderRef ! BankTransactionResponse(request.ref, request.transaction, None)
        }
        case Failure(t) =>
          logger.error(s"Error retrieving account for: ${request.transaction}", t)
          senderRef ! BankTransactionResponse(request.ref, request.transaction, None)
      }

    case account: DBBankAccount => connection.save(account)

  }
}
