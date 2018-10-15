package com.ogunleye.tenii.products.model.db

import org.bson.types.ObjectId

case class TellerDebitTransaction(id: Option[ObjectId] = None, userId: String, tellerAccountId: String,
                                  tellerTransactionId:String, amount: Double, date: String)
