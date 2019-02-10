package com.ogunleye.tenii.products.actors

import akka.actor.Actor
import com.ogunleye.tenii.products.db.BankAccountConnection
import com.ogunleye.tenii.products.model.api._
import com.ogunleye.tenii.products.model.implicits.AccountImplicit
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class BankAccountActor extends Actor with LazyLogging with AccountImplicit {

  val connection = new BankAccountConnection

  override def receive: Receive = {

    case request: SourceBankAccount =>
      val senderRef = sender()
      Future {
        connection.save(request)
      } onComplete {
        case Success(_) => logger.info(s"Saved source bank account for request: $request")
          senderRef ! SourceBankAccountResponse(Some(request.accountId))
        case Failure(t) => logger.error(s"Error thrown while trying to save: $request", t)
          senderRef ! ErrorResponse("SAVE_ERROR", Some("Error thrown while trying to save"))
      }

    case request: GetBankAccountRequest =>
      val senderRef = sender()
      Future {
        connection.findByUserId(request.teniiId)
      } onComplete {
        case Success(accOpt) => accOpt match {
          case Some(acc) => senderRef ! SourceBankAccountResponse(Some(acc.accountId), Some(acc.teniiId))
          case None => senderRef ! ErrorResponse("NO_USER", Some(s"No account for user: ${request.teniiId}"))
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
        case Failure(t) => logger.error(s"Failure when looking up account for transaction: $trans", t)
          senderRef ! None
      }
    case other => logger.error(s"Unknown message received: $other")
  }
}
