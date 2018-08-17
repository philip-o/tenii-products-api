package com.ogunleye.tenii.products.routes

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.{ CircuitBreaker, ask }
import akka.util.Timeout
import com.ogunleye.tenii.products.actors.TransactionActor
import com.ogunleye.tenii.products.model.api.temp.{ AddTransactionResponse, Transaction }
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

@Path("/transaction")
class TransactionRoute(implicit system: ActorSystem, breaker: CircuitBreaker) extends RequestDirectives with LazyLogging {

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)
  protected val transactionActor: ActorRef = system.actorOf(Props[TransactionActor])

  def route: Route = pathPrefix("transaction") {
    addTransaction
  }

  def addTransaction: Route =
    post {
      entity(as[Transaction]) { request =>
        logger.info(s"POST /transaction - $request")
        onCompleteWithBreaker(breaker)(transactionActor ? request) {
          case Success(msg: AddTransactionResponse) => complete(StatusCodes.Created -> msg)
          case Failure(t) => failWith(t)
        }
      }
    }
}
