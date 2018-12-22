package com.ogunleye.tenii.products.model.api

case class SourceBankAccount(accountId: String, teniiId: String)

case class SourceBankAccountResponse(accountId: Option[String], teniiId: Option[String])

case class SourceBankAccountErrorResponse(error: String)

case class GetBankAccountRequest(teniiId: String)