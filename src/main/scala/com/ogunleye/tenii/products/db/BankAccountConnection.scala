package com.ogunleye.tenii.products.db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.ogunleye.tenii.products.model.db.BankAccount
import com.typesafe.scalalogging.LazyLogging

class BankAccountConnection extends ObjectMongoConnection[BankAccount] with LazyLogging {

  val collection = "bankAccount"

  override def transform(obj: BankAccount): MongoDBObject = {
    MongoDBObject("_id" -> obj.id, "teniiId" -> obj.teniiId, "accountId" -> obj.accountId)
  }

  def findByUserId(teniiId: String): Option[BankAccount] = {
    findByProperty("teniiId", teniiId, s"No bank account found with teniiId: $teniiId")
  }

  def findByAccountId(accountId: String): Option[BankAccount] = {
    findByProperty("accountId", accountId, s"No bank account found with accountId: $accountId")
  }

  def findById(id: String): Option[BankAccount] =
    findByObjectId(id, s"No bank account found with id: $id")

  override def revert(obj: MongoDBObject): BankAccount = {
    BankAccount(
      Some(getObjectId(obj, "_id")),
      getString(obj, "teniiId"),
      getString(obj, "accountId")
    )
  }

}
