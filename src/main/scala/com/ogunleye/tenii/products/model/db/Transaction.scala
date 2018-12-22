package com.ogunleye.tenii.products.model.db

import org.bson.types.ObjectId

case class Transaction(id: Option[ObjectId] = None, userId: String, provider: String, sortCode: String, accountNumber: String, date: String, amount: Double)
