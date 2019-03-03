package com.ogunleye.tenii.products.external

import com.ogunleye.tenii.products.config.Config

trait PaymentEndpoints {

  val paymentsApiHost = Config.teniiPayments
  val updatePot = Config.creditPath

  implicit def onSuccessDecodingError[TellerTeniiPaymentsResponse](decodingError: io.circe.Error): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding trains upstream response: $decodingError")
  implicit def onErrorDecodingError[TellerTeniiPaymentsResponse](decodingError: String): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding upstream error response: $decodingError")
}