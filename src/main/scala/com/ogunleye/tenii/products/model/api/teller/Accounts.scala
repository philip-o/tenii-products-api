package com.ogunleye.tenii.products.model.api.teller

case class TellerAccountResponse(
  name: String,
  links: Links,
  institution: String,
  id: String,
  enrollment_id: String,
  customer_type: String,
  currency: String,
  bank_code: String,
  balance: String,
  account_number: String
)

case class Links(transactions: String)

case class TellerAccountsRequest(id: String)