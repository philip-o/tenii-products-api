package com.ogunleye.tenii.products.db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.ogunleye.tenii.products.model.db.TellerDebitTransaction
import com.typesafe.scalalogging.LazyLogging

class TellerTransactionConnection extends ObjectMongoConnection[TellerDebitTransaction] with LazyLogging {

  val collection = "tellerTransactions"

  override def transform(obj: TellerDebitTransaction): MongoDBObject = {
    MongoDBObject("_id" -> obj.id, "userId" -> obj.userId, "tellerAccountId" -> obj.tellerAccountId,
      "tellerTransactionId" -> obj.tellerTransactionId, "amount" -> obj.amount, "date" -> obj.date)
  }

  def findByUserId(userId: String): Option[TellerDebitTransaction] = {
    findByProperty("userId", userId, s"No teller transaction found with userId: $userId")
  }

  def findByTellerAccount(accountId: String): Option[TellerDebitTransaction] = {
    findByProperty("accountId", accountId, s"No teller transaction found with accountId: $accountId")
  }

  def findByTransactionId(transactionId: String) : Option[TellerDebitTransaction] = {
    findByProperty("tellerTransactionId", transactionId, s"No teller transaction found with tellerTransactionId: $transactionId")
  }

  def findById(id: String): Option[TellerDebitTransaction] =
    findByObjectId(id, s"No teller transaction found with id: $id")

  override def revert(obj: MongoDBObject): TellerDebitTransaction = {
    TellerDebitTransaction(
      Some(getObjectId(obj, "_id")),
      getString(obj, "userId"),
      getString(obj, "tellerAccountId"),
      getString(obj, "tellerTransactionId"),
      getDouble(obj, "amount"),
      getString(obj, "date")
    )
  }
}
