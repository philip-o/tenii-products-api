package com.ogunleye.tenii.products.db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.ogunleye.tenii.products.model.db.BankAccount
import com.typesafe.scalalogging.LazyLogging

class BankAccountConnection extends ObjectMongoConnection[BankAccount] with LazyLogging {

  val collection = "bankAccount"

  override def transform(obj: BankAccount): MongoDBObject = {
    MongoDBObject("_id" -> obj.id, "userId" -> obj.userId, "provider" -> obj.provider, "sortCode" -> obj.sortCode,
      "accountNumber" -> obj.accountNumber, "balance" -> obj.balance)
  }

  def findByUserId(userId: String): List[BankAccount] = {
    findAllByProperty("userId", userId, s"No bank accounts found with userId: $userId")
  }

  def findByAccountNumber(accountNumber: String): Option[BankAccount] = {
    findByProperty("accountNumber", accountNumber, s"No bank account found with accountNumber: $accountNumber")
  }

  def findByAccountNumberUserId(accountNumber: String, userId: String): Option[BankAccount] = {
    findByProperties(
      List(("accountNumber", accountNumber), ("userId", userId)),
      s"No bank account found with accountNumber: $accountNumber and userId $userId"
    )
  }

  def findById(id: String): Option[BankAccount] =
    findByObjectId(id, s"No bank account found with id: $id")

  override def revert(obj: MongoDBObject): BankAccount = {
    BankAccount(
      Some(getObjectId(obj, "_id")),
      getString(obj, "userId"),
      getString(obj, "provider"),
      getString(obj, "sortCode"),
      getString(obj, "accountNumber"),
      getDouble(obj, "balance")
    )
  }

}
