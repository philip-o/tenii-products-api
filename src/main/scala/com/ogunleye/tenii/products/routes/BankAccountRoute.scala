package com.ogunleye.tenii.products.routes

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.{CircuitBreaker, ask}
import akka.util.Timeout
import com.ogunleye.tenii.products.actors.BankAccountActor
import com.ogunleye.tenii.products.model.api._
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.swagger.annotations._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

@Path("/bankAccount")
@Api(value = "/bankAccount", description = "Route to add and get source bank details", produces = "application/json")
class BankAccountRoute(implicit system: ActorSystem, breaker: CircuitBreaker) extends RequestDirectives with LazyLogging {

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)
  protected val bankAccountActor: ActorRef = system.actorOf(Props[BankAccountActor])

  def route: Route = pathPrefix("bankAccount") {
    addBankAccount ~ getBankAccount
  }

  @ApiOperation(
    httpMethod = "POST",
    response = classOf[SourceBankAccountResponse],
    value = "Add source bank account for user",
    consumes = "application/json",
    notes =
      """
         Add source bank account for user
      """
  )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "teniiId", dataType = "string", value = "The tenii Id for the user to find their account", paramType = "body", required = true),
    new ApiImplicitParam(name = "accountId", dataType = "java.lang.String", paramType = "body", value = "The id for the user with the SLC", required = true)
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "Created", response = classOf[SourceBankAccountResponse]),
    new ApiResponse(code = 500, message = "Internal Server Error", response = classOf[Throwable])
  ))
  def addBankAccount: Route =
    post {
      entity(as[SourceBankAccount]) { request =>
        logger.info(s"POST /bankAccount - $request")
        onCompleteWithBreaker(breaker)(bankAccountActor ? request) {
          case Success(msg: SourceBankAccountResponse) => complete(StatusCodes.Created -> msg)
          case Failure(t) => failWith(t)
        }
      }
    }

  @Path("{teniiId}")
  @ApiOperation(
    httpMethod = "GET",
    response = classOf[SourceBankAccountResponse],
    value = "Get source bank account for user",
    consumes = "application/json",
    notes =
      """
         Get source bank account for user
      """
  )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "teniiId", dataType = "string", value = "The tenii Id for the user to find their account", paramType = "body", required = true)
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "Created", response = classOf[SourceBankAccountResponse]),
    new ApiResponse(code = 400, message = "Bad Request", response = classOf[SourceBankAccountResponse]),
    new ApiResponse(code = 500, message = "Internal Server Error", response = classOf[Throwable])
  ))
  def getBankAccount: Route =
    get {
      path(userIdSegment).as(GetBankAccountRequest) {
        request =>
          logger.info(s"GET /bankAccount - $request")
          onCompleteWithBreaker(breaker)(bankAccountActor ? request) {
            case Success(msg: SourceBankAccountResponse) => complete(StatusCodes.OK -> msg)
            case Success(msg: SourceBankAccountErrorResponse) => complete(StatusCodes.BadRequest -> msg)
            case Failure(t) => failWith(t)
          }
      }
    }
}
