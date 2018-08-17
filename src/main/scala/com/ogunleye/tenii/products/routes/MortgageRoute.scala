package com.ogunleye.tenii.products.routes

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.{ CircuitBreaker, ask }
import akka.util.Timeout
import com.ogunleye.tenii.products.actors.MortgageActor
import com.ogunleye.tenii.products.model.api.{ AddMortgageResponse, GetMortgageRequest, GetMortgageResponse, Mortgage }
import com.typesafe.scalalogging.LazyLogging
import javax.ws.rs.Path
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

@Path("/mortgage")
class MortgageRoute(implicit system: ActorSystem, breaker: CircuitBreaker) extends RequestDirectives with LazyLogging {

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)
  protected val mortgageActor: ActorRef = system.actorOf(Props(classOf[MortgageActor]))

  def route: Route = pathPrefix("mortgage") {
    addMortgage ~ getMortgage
  }

  def addMortgage: Route =
    post {
      entity(as[Mortgage]) { request =>
        logger.info(s"POST /mortgage - $request")
        onCompleteWithBreaker(breaker)(mortgageActor ? request) {
          case Success(msg: AddMortgageResponse) => complete(StatusCodes.Created -> msg)
          case Failure(t) => failWith(t)
        }
      }
    }

  def getMortgage: Route =
    get {
      (pathEnd & userIdDirective).as(GetMortgageRequest) {
        request =>
          logger.info(s"GET /mortgage - $request")
          onCompleteWithBreaker(breaker)(mortgageActor ? request) {
            case Success(msg: GetMortgageResponse) if msg.cause.isEmpty => complete(StatusCodes.OK -> msg.mortgage)
            case Failure(t) => failWith(t)
          }
      }
    }
}
