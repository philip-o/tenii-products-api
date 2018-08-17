package com.ogunleye.tenii.products.db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.ogunleye.tenii.products.model.TeniiDate
import com.ogunleye.tenii.products.model.db.Transaction
import com.typesafe.scalalogging.LazyLogging

class TransactionConnection extends ObjectMongoConnection[Transaction] with LazyLogging {

  val collection = "transaction"

  override def transform(obj: Transaction): MongoDBObject = {
    MongoDBObject("_id" -> obj.id, "userId" -> obj.userId, "provider" -> obj.provider, "sortCode" -> obj.sortCode,
      "accountNumber" -> obj.accountNumber, "date" -> obj.date, "amount" -> obj.amount)
  }

  def findByAccountNumber(accountNumber: String): List[Transaction] = {
    findAllByProperty("accountNumber", accountNumber, s"No transactions found with accountNumber: $accountNumber")
  }

  def findById(id: String): Option[Transaction] =
    findByObjectId(id, s"No transaction found with id: $id")

  override def revert(obj: MongoDBObject): Transaction = {
    Transaction(
      Some(getObjectId(obj, "_id")),
      getString(obj, "userId"),
      getString(obj, "provider"),
      getString(obj, "sortCode"),
      getString(obj, "accountNumber"),
      getString(obj, "date"),
      getDouble(obj, "amount")
    )
  }
}