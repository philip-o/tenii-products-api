package com.ogunleye.tenii.products.model.db

import org.bson.types.ObjectId

case class BankAccount(id: Option[ObjectId] = None, teniiId: String, accountId: String)

case class SourceBankAccounts(id: Option[ObjectId] = None, teniiId: String, accountIds: Set[String])
