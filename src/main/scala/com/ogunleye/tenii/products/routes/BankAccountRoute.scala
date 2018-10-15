package com.ogunleye.tenii.products.routes

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.{ CircuitBreaker, ask }
import akka.util.Timeout
import com.ogunleye.tenii.products.actors.BankAccountActor
import com.ogunleye.tenii.products.model.api.{ AddBankAccountResponse, BankAccount, GetBankAccountRequest, GetBankAccountResponse }
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

@Path("/bankAccount")
class BankAccountRoute(implicit system: ActorSystem, breaker: CircuitBreaker) extends RequestDirectives with LazyLogging {

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)
  protected val bankAccountActor: ActorRef = system.actorOf(Props[BankAccountActor])

  def route: Route = pathPrefix("bankAccount") {
    addBankAccount ~ getBankAccount
  }

  def addBankAccount: Route =
    post {
      entity(as[BankAccount]) { request =>
        logger.info(s"POST /bankAccount - $request")
        onCompleteWithBreaker(breaker)(bankAccountActor ? request) {
          case Success(msg: AddBankAccountResponse) => complete(StatusCodes.Created -> msg)
          case Failure(t) => failWith(t)
        }
      }
    }

  def getBankAccount: Route =
    get {
      (pathEnd & userIdDirective).as(GetBankAccountRequest) {
        request =>
          logger.info(s"GET /bankAccount - $request")
          onCompleteWithBreaker(breaker)(bankAccountActor ? request) {
            case Success(msg: GetBankAccountResponse) if msg.cause.isEmpty => complete(StatusCodes.OK -> msg.account)
            case Failure(t) => failWith(t)
          }
      }
    }
}