package com.ogunleye.tenii.products.external

import com.ogunleye.tenii.products.config.TeniiConfig

trait PaymentEndpoints {

  val paymentsApiHost = TeniiConfig.teniiPayments
  val updatePot = TeniiConfig.creditPath

  implicit def onSuccessDecodingError[TellerTeniiPaymentsResponse](decodingError: io.circe.Error): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding trains upstream response: $decodingError")
  implicit def onErrorDecodingError[TellerTeniiPaymentsResponse](decodingError: String): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding upstream error response: $decodingError")
}