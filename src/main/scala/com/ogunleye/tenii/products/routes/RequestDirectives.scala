package com.ogunleye.tenii.products.routes

import akka.http.scaladsl.server.{ Directive1, Directives, PathMatcher1 }

trait RequestDirectives extends Directives {

  val userIdSegment: PathMatcher1[String] = Segment
  val accountIdSegment: PathMatcher1[String] = Segment
  val userIdDirective: Directive1[String] = parameter("userId")
}
