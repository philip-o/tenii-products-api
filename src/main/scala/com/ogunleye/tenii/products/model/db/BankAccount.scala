package com.ogunleye.tenii.products.model.db

import org.bson.types.ObjectId

case class BankAccount(id: Option[ObjectId] = None, teniiId: String, accountId: String)
