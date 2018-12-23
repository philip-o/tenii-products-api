package com.ogunleye.tenii.products.model.db

import org.bson.types.ObjectId

case class Transaction(id: Option[ObjectId] = None, transactionId: String, accountId: String, teniiId: String, amount: Double, date: String)
