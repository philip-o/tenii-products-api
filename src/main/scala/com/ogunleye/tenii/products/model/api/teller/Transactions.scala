package com.ogunleye.tenii.products.model.api.teller

case class TellerTransaction(
  running_balance: String,
  description: String,
  date: String,
  id: String,
  counterparty: String,
  amount: String
)

//{
//  "type": "digital_payment",
//  "running_balance": null,
//  "links": {
//    "self": "https://api.teller.io/accounts/39252363-65d1-46e1-b9f7-07af3076bd21/transactions/057f65c6-25b2-41b6-9149-313a71932d25"
//  },
//  "id": "057f65c6-25b2-41b6-9149-313a71932d25",
//  "description": "TO A/C    64757560",
//  "date": "2018-09-24",
//  "counterparty": "TO A/C    64757560",
//  "amount": "-10.00"
//}
case class TellerTransactionsResponse(transactions: List[TellerTransaction])

case class TellerFailure(cause: String)

case class TellerTransactionsRequest(id: String, accountId: String)

case class TellerTeniiPaymentsResponse(id: String, success: Boolean, cause: Option[String])

case class TellerTeniiPotCreditRequest(amount: Double, accountId: String)
