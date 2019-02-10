package com.ogunleye.tenii.products.model.api

case class SourceBankAccount(accountId: String, teniiId: String)

case class SourceBankAccountResponse(accountId: Option[String])

case class ErrorResponse(code: String, msg: Option[String] = None)

case class GetBankAccountRequest(teniiId: String)