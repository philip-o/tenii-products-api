package com.ogunleye.tenii.products.db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.ogunleye.tenii.products.model.TeniiDate
import com.ogunleye.tenii.products.model.db.Transaction
import com.typesafe.scalalogging.LazyLogging

class TransactionConnection extends ObjectMongoConnection[Transaction] with LazyLogging {

  val collection = "transaction"

  override def transform(obj: Transaction): MongoDBObject = {
    MongoDBObject("_id" -> obj.id, "transactionId" -> obj.transactionId, "accountId" -> obj.accountId, "teniiId" -> obj.teniiId,
      "date" -> obj.date, "amount" -> obj.amount)
  }

  def findByAccountId(accountId: String): Option[Transaction] = {
    findByProperty("accountId", accountId, s"No transaction found with accountId: $accountId")
  }

  def findByTeniiId(teniiId: String): Option[Transaction] = {
    findByProperty("teniiId", teniiId, s"No transaction found with teniiId: $teniiId")
  }

  def findById(id: String): Option[Transaction] =
    findByObjectId(id, s"No transaction found with id: $id")

  override def revert(obj: MongoDBObject): Transaction = {
    Transaction(
      Some(getObjectId(obj, "_id")),
      getString(obj, "transactionId"),
      getString(obj, "accountId"),
      getString(obj, "teniiId"),
      getDouble(obj, "amount"),
      getString(obj, "date")
    )
  }
}