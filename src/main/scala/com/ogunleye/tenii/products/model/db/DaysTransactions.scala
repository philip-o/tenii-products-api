package com.ogunleye.tenii.products.model.db

import org.bson.types.ObjectId

case class DaysTransactions(id: Option[ObjectId] = None, transactionIds: List[String], accountId: String, teniiId: String, amount: Double, date: String)
