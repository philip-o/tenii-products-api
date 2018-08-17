package com.ogunleye.tenii.products.model.db

import org.bson.types.ObjectId

case class BankAccount(id: Option[ObjectId] = None, userId: String, provider: String, sortCode: String, accountNumber: String, balance: Double)
