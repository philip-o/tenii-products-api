package com.ogunleye.tenii.products.model.api

case class Transaction(transactionId: String, accountId: String, teniiId: String, amount: Double, date: String)

case class ProcessTransactionResponse(transactionId: String, error: Option[String] = None)


case class GetTransactionResponse(transactionIds: List[String], teniiId: String, date: Option[String])

case class GetTransactionErrorResponse(error: String)

case class GetTransactionRequest(teniiId: String, accountId: String)