package com.ogunleye.tenii.products.routes

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.{CircuitBreaker, ask}
import akka.util.Timeout
import com.ogunleye.tenii.products.actors.TransactionActor
import com.ogunleye.tenii.products.model.api._
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

@Path("/transaction")
class TransactionRoute(implicit system: ActorSystem, breaker: CircuitBreaker) extends RequestDirectives with LazyLogging {

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(20.seconds)
  protected val transactionActor: ActorRef = system.actorOf(Props[TransactionActor])

  def route: Route = pathPrefix("transaction") {
    processTransaction ~ getLastTransaction
  }

  def processTransaction: Route =
    post {
      entity(as[Transaction]) { request =>
        logger.info(s"POST /transaction - $request")
        onCompleteWithBreaker(breaker)(transactionActor ? request) {
          case Success(msg: ProcessTransactionResponse) if msg.error.nonEmpty => complete(StatusCodes.BadRequest -> msg)
          case Success(msg: ProcessTransactionResponse) => complete(StatusCodes.Created -> msg)
          case Failure(t) => failWith(t)
        }
      }
    }

  def getLastTransaction : Route =
    get {
      path(userIdSegment / accountIdSegment).as(GetTransactionRequest) {
        request =>
          logger.info(s"GET /transaction - $request")
          onCompleteWithBreaker(breaker)(transactionActor ? request) {
            case Success(msg: GetTransactionResponse) => complete(StatusCodes.OK -> msg)
            case Success(msg: GetTransactionErrorResponse) => complete(StatusCodes.BadRequest -> msg)
            case Failure(t) => failWith(t)
          }
      }
    }
}
