package com.ogunleye.tenii.products.model.db

import com.ogunleye.tenii.products.model.TeniiDate
import com.ogunleye.tenii.products.model.api.TeniiProduct
import org.bson.types.ObjectId

case class Mortgage(id: Option[ObjectId] = None, userId: String, provider: Option[String] = None, mortgageType: String, monthlyPayment: Double, accountNumber: Option[Int] = None,
  productStarted: String, rate: Double, balanceAtYearStart: Option[Double], dateFixedProductEnds: Option[String], balance: Double) extends TeniiProduct
