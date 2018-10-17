package com.ogunleye.tenii.products.routes

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.{ CircuitBreaker, ask }
import akka.util.Timeout
import com.ogunleye.tenii.products.actors.TellerActor
import com.ogunleye.tenii.products.model.api.teller._
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

@Path("/teller")
class TellerRoute(implicit system: ActorSystem, breaker: CircuitBreaker) extends RequestDirectives with LazyLogging {

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)
  protected val tellActor: ActorRef = system.actorOf(Props[TellerActor])

  def route: Route = pathPrefix("teller") {
    getAccounts ~ getTransactions
  }

  def getAccounts: Route =
    post {
      (path("bankAccounts") & entity(as[TellerAccountsRequest])) { request =>
        logger.info(s"POST /teller/bankAccounts - $request")
        onCompleteWithBreaker(breaker)(tellActor ? request) {
          case Success(msg: List[TellerAccountResponse]) => complete(StatusCodes.OK -> msg)
          case Success(other: Exception) => complete(StatusCodes.InternalServerError -> other)
          case Failure(t) => failWith(t)
        }
      }
    }

  def getTransactions: Route =
    post {
      (path("transactions") & entity(as[TellerTransactionsRequest])) { request =>
        logger.info(s"POST /teller/transactions - $request")
        onCompleteWithBreaker(breaker)(tellActor ? request) {
          case Success(msg: List[TellerTransaction]) => complete(StatusCodes.OK -> TellerTransactionsResponse(msg))
          case Success(other: Exception) => complete(StatusCodes.InternalServerError -> other)
          case Failure(t) => failWith(t)
        }
      }
    }
}
