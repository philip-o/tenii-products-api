package com.ogunleye.tenii.products.external

trait PaymentEndpoints {

  val paymentsApiHost = "https://tenii-payments-api.heroku.com/"
  val updatePot = "credit"

  implicit def onSuccessDecodingError[TellerTeniiPaymentsResponse](decodingError: io.circe.Error): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding trains upstream response: $decodingError")
  implicit def onErrorDecodingError[TellerTeniiPaymentsResponse](decodingError: String): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding upstream error response: $decodingError")
}