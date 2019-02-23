package com.ogunleye.tenii.products.db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.ogunleye.tenii.products.model.db.SourceBankAccounts
import com.typesafe.scalalogging.LazyLogging

class SourceBankAccountsConnection extends ObjectMongoConnection[SourceBankAccounts] with LazyLogging {

  val collection = "sourceBankAccount"

  override def transform(obj: SourceBankAccounts): MongoDBObject =
    MongoDBObject("_id" -> obj.id, "teniiId" -> obj.teniiId, "accountIds" -> obj.accountIds)

  def findByUserId(teniiId: String): Option[SourceBankAccounts] =
    findByProperty("teniiId", teniiId, s"No bank accounts found with teniiId: $teniiId")

  def findById(id: String): Option[SourceBankAccounts] =
    findByObjectId(id, s"No bank accounts found with id: $id")

  override def revert(obj: MongoDBObject): SourceBankAccounts = {
    SourceBankAccounts(
      Some(getObjectId(obj, "_id")),
      getString(obj, "teniiId"),
      getList[String](obj, "accountIds").toSet
    )
  }
}
