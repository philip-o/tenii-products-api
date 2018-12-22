package com.ogunleye.tenii.products.actors

import akka.actor.{Actor, ActorRef}
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
          senderRef ! SourceBankAccountResponse(Some(request.accountId), Some(request.teniiId))
        case Failure(t) => logger.error(s"Error thrown while trying to save: $request", t)
          senderRef ! SourceBankAccountErrorResponse("Error thrown while trying to save")
      }

    case request: GetBankAccountRequest =>
      val senderRef = sender()
      Future {
        connection.findByUserId(request.teniiId)
      } onComplete {
        case Success(accOpt) => accOpt match {
          case Some(acc) => senderRef ! SourceBankAccountResponse(Some(acc.accountId), Some(acc.teniiId))
          case None => senderRef ! SourceBankAccountResponse(None, None)
            logger.info("No account for user")
        }
        case Failure(t) => logger.error("Failed to lookup account", t)
          senderRef ! SourceBankAccountErrorResponse("Failed to lookup account")
      }

    case other => logger.error(s"Unknown message received: $other")
  }
}
