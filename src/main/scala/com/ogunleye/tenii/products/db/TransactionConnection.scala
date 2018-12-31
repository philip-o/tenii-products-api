package com.ogunleye.tenii.products.db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.ogunleye.tenii.products.model.TeniiDate
import com.ogunleye.tenii.products.model.db.DaysTransactions
import com.typesafe.scalalogging.LazyLogging

class TransactionConnection extends ObjectMongoConnection[DaysTransactions] with LazyLogging {

  val collection = "transaction"

  override def transform(obj: DaysTransactions): MongoDBObject = {
    MongoDBObject("_id" -> obj.id, "transactionIds" -> obj.transactionIds, "accountId" -> obj.accountId, "teniiId" -> obj.teniiId,
      "date" -> obj.date, "amount" -> obj.amount)
  }

  def findByAccountId(accountId: String): Option[DaysTransactions] = {
    findByProperty("accountId", accountId, s"No transaction found with accountId: $accountId")
  }

  def findByTeniiId(teniiId: String): Option[DaysTransactions] = {
    findByProperty("teniiId", teniiId, s"No transaction found with teniiId: $teniiId")
  }

  def findById(id: String): Option[DaysTransactions] =
    findByObjectId(id, s"No transaction found with id: $id")

  override def revert(obj: MongoDBObject): DaysTransactions = {
    DaysTransactions(
      Some(getObjectId(obj, "_id")),
      getList[String](obj, "transactionIds"),
      getString(obj, "accountId"),
      getString(obj, "teniiId"),
      getDouble(obj, "amount"),
      getString(obj, "date")
    )
  }
}