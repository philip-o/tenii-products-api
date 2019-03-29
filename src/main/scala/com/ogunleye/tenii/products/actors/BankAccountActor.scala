package com.ogunleye.tenii.products.actors

import akka.actor.Actor
import com.ogunleye.tenii.products.db.{BankAccountConnection, SourceBankAccountsConnection}
import com.ogunleye.tenii.products.model.api._
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
        connection.findByUserId(request.teniiId)
      } onComplete {
        case Success(source) => source match {
          case Some(_) => senderRef ! ErrorResponse("SAVE_ERROR", Some("Source bank already exists"))
          case None => connection.save(request)
            senderRef ! SourceBankAccountResponse(request.accountId)
        }
          logger.info(s"Saved source bank account for request: $request")
        case Failure(t) => logger.error(s"Error thrown while trying to search: $request", t)
          senderRef ! ErrorResponse("SEARCH_ERROR", Some("Error thrown while trying to search"))
      }

    case request: GetBankAccountRequest =>
      val senderRef = sender()
      Future {
        connection.findByUserId(request.teniiId)
      } onComplete {
        case Success(accOpt) => accOpt match {
          case Some(acc) => senderRef ! SourceBankAccountResponse(acc.accountId)
          case None => senderRef ! ErrorResponse("USER_ERROR", Some("No source account"))
            logger.info(s"No account for user: ${request.teniiId}")
        }
        case Failure(t) => logger.error("Failed to lookup account", t)
          senderRef ! ErrorResponse("SEARCH_ERROR", Some("Failed to lookup account"))
      }
    case trans: Transaction =>
      val senderRef = sender()
      Future {
        connection.findByUserId(trans.teniiId)
      } onComplete {
        case Success(acc) => senderRef ! acc
        case Failure(t) => senderRef ! None
          logger.error(s"Failure when looking up account for transaction: $trans", t)
      }
    case other => logger.error(s"Unknown message received: $other")
  }
}
