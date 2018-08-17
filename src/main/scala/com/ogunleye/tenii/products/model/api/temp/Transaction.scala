package com.ogunleye.tenii.products.model.api.temp

import com.ogunleye.tenii.products.model.TeniiDate

case class Transaction(userId: String, provider: String, sortCode: String, accountNumber: String, date: String, amount: Double)

case class AddTransactionResponse(userId: String, cause: Option[String] = None)
