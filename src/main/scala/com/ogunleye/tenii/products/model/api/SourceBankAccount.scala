package com.ogunleye.tenii.products.model.api

case class SourceBankAccount(accountId: String, teniiId: String)

case class SourceBankAccountsResponse(accountIds: Set[String])

case class HasSourceBankAccountResponse(teniiId: String, hasAccount: Boolean)

case class ErrorResponse(code: String, msg: Option[String] = None)

case class GetBankAccountRequest(teniiId: String)