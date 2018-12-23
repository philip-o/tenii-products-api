package com.ogunleye.tenii.products.helpers

object NumberHelper {

  def roundTo2DPAsDouble(value: Double): Double = {
    BigDecimal(value).setScale(2, BigDecimal.RoundingMode.UP).toDouble
  }

  def roundTo2DPAsDouble(value: Int): Double = {
    BigDecimal(value).setScale(2, BigDecimal.RoundingMode.UP).toDouble
  }
}
