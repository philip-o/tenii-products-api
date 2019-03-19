package com.ogunleye.tenii.products.actors

import akka.actor.Actor
import com.ogunleye.tenii.products.db.{BankAccountConnection, SourceBankAccountsConnection}
import com.ogunleye.tenii.products.model.api._
import com.ogunleye.tenii.products.model.db.BankAccount
import com.ogunleye.tenii.products.model.implicits.AccountImplicit
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class BankAccountActor extends Actor with LazyLogging with AccountImplicit {

  val connection = new BankAccountConnection
  val sourceBankAccountConnection = new SourceBankAccountsConnection

  override def receive: Receive = {

    case request: SourceBankAccount =>
      //TODO search if account already set
      val senderRef = sender()
      Future {
        sourceBankAccountConnection.findByUserId(request.teniiId)
      } onComplete {
        case Success(source) => source match {
          case Some(acc) => sourceBankAccountConnection.save(acc.copy(accountIds = acc.accountIds. +(request.accountId)))
            senderRef ! SourceBankAccountsResponse(acc.accountIds. +(request.accountId))
          case None => sourceBankAccountConnection.save(request)
            senderRef ! SourceBankAccountsResponse(Set(request.accountId))
        }
          logger.info(s"Saved source bank account for request: $request")
        case Failure(t) => logger.error(s"Error thrown while trying to search: $request", t)
          senderRef ! ErrorResponse("SEARCH_ERROR", Some("Error thrown while trying to search"))
      }

    case request: GetBankAccountRequest =>
      val senderRef = sender()
      Future {
        sourceBankAccountConnection.findByUserId(request.teniiId)
      } onComplete {
        case Success(accOpt) => accOpt match {
          case Some(acc) => senderRef ! SourceBankAccountsResponse(acc.accountIds)
          case None => senderRef ! SourceBankAccountsResponse(Set.empty)
            logger.info(s"No account for user: ${request.teniiId}")
        }
        case Failure(t) => logger.error("Failed to lookup account", t)
          senderRef ! ErrorResponse("SEARCH_ERROR", Some("Failed to lookup account"))
      }
    case trans: Transaction =>
      val senderRef = sender()
      Future {
        sourceBankAccountConnection.findByUserId(trans.teniiId)
      } onComplete {
        case Success(accOpt) => accOpt match {
          case Some(acc) => senderRef ! acc.accountIds.find(ids => ids == trans.accountId).map(accId => BankAccount(acc.id, acc.teniiId, accId))
          case _ => senderRef ! None
        }
        case Failure(t) => logger.error(s"Failure when looking up account for transaction: $trans", t)
          senderRef ! None
      }
    case other => logger.error(s"Unknown message received: $other")
  }
}
