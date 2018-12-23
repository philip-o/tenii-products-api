package com.ogunleye.tenii.products.model.api

case class TeniiPotCreditRequest(teniiId: String, amount: Double)

case class Pot(teniiId: String, limit: Int, amount: Double = 0)

case class TeniiPotCreditResponse(pot: Option[Pot], cause: Option[String] = None)