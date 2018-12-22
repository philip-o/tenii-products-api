package com.ogunleye.tenii.products.actors

import akka.actor.Actor
import com.ogunleye.tenii.products.db.MortgageConnection
//import com.ogunleye.tenii.products.model.api.{ AddMortgageResponse, GetMortgageRequest, GetMortgageResponse, Mortgage }
import com.ogunleye.tenii.products.model.db.{ Mortgage => DBMortgage }
import com.ogunleye.tenii.products.model.implicits.MortgageImplicit
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

class MortgageActor extends Actor with LazyLogging with MortgageImplicit {

  val connection = new MortgageConnection

  override def receive: Receive = {
//    case mortgage: Mortgage if mortgage.mortgageType == "Fixed" =>
//      val senderRef = sender()
//      Future {
//        connection.save(mortgage)
//      } onComplete {
//        case Success(_) => senderRef ! AddMortgageResponse(mortgage.userId)
//        case Failure(t) =>
//          logger.error(s"Error saving mortgage for user: $mortgage", t)
//          senderRef ! AddMortgageResponse(mortgage.userId, Some(t.getMessage))
//      }
//
//    case request: GetMortgageRequest =>
//      val senderRef = sender()
//      Future {
//        connection.findByUserId(request.userId)
//      } onComplete {
//        case Success(mortgageOption) => mortgageOption match {
//          case Some(mortgage) => senderRef ! GetMortgageResponse(Some(mortgage), None)
//          case None => senderRef ! GetMortgageResponse(None, Some(s"No mortgage exists for user ${request.userId}"))
//        }
//        case Failure(t) =>
//          logger.error(s"Error retrieving mortgage for user: ${request.userId}", t)
//          senderRef ! GetMortgageResponse(None, Some(s"Error retrieving mortgage for user due to: ${t.getCause}"))
//      }
//
//    case mortgage: DBMortgage =>
//      logger.info(s"Received updated mortgage, saving: $mortgage")
//      connection.save(mortgage)
//
//    case mortgage: Mortgage if mortgage.mortgageType == "Variable" =>
//
//    case mortgage: Mortgage => logger.error(s"Unknown mortgage type, cannot process: $mortgage")

    case other => logger.error(s"Unknown message received: $other")
  }
}
