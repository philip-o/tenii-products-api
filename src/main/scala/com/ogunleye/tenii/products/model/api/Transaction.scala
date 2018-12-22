package com.ogunleye.tenii.products.model.api

case class Transaction(transactionId: String, accountId: String, teniiId: String, amount: Double, date: String)

case class ProcessTransactionResponse(transactionId: String, error: Option[String] = None)