package com.ogunleye.tenii.products.db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.ogunleye.tenii.products.model.TeniiDate
import com.ogunleye.tenii.products.model.db.Mortgage
import com.typesafe.scalalogging.LazyLogging

class MortgageConnection extends ObjectMongoConnection[Mortgage] with LazyLogging {

  val collection = "mortgages"

  override def transform(obj: Mortgage): MongoDBObject = {
    MongoDBObject("_id" -> obj.id, "userId" -> obj.userId, "provider" -> obj.provider, "mortgageType" -> obj.mortgageType, "monthlyPayment" -> obj.monthlyPayment, "accountNumber" -> obj.accountNumber,
      "productStarted" -> obj.productStarted, "rate" -> obj.rate, "balanceAtYearStart" -> obj.balanceAtYearStart, "dateFixedProductEnds" -> obj.dateFixedProductEnds, "balance" -> obj.balance)
  }

  def findByUserId(userId: String): Option[Mortgage] = {
    findByProperty("userId", userId, s"No mortgage found with userId: $userId")
  }

  def findByAccountNumber(accountNumber: Int): Option[Mortgage] = {
    findByProperty("accountNumber", accountNumber, s"No mortgage found with accountNumber: $accountNumber")
  }

  def findById(id: String): Option[Mortgage] =
    findByObjectId(id, s"No mortgage found with id: $id")

  override def revert(obj: MongoDBObject): Mortgage = {
    Mortgage(
      Some(getObjectId(obj, "_id")),
      getString(obj, "userId"),
      getOptional[String](obj, "provider"),
      getString(obj, "mortgageType"),
      getDouble(obj, "monthlyPayment"),
      getOptional[Int](obj, "accountNumber"),
      getString(obj, "productStarted"),
      getDouble(obj, "rate"),
      getOptional[Double](obj, "balanceAtYearStart"),
      getOptional[String](obj, "dateFixedProductEnds"),
      getDouble(obj, "balance")
    )
  }
}