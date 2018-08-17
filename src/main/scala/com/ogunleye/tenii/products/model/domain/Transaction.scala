package com.ogunleye.tenii.products.model.domain

import akka.actor.ActorRef
import com.ogunleye.tenii.products.model.api.temp.Transaction
import com.ogunleye.tenii.products.model.db.{ Mortgage, BankAccount }

case class BankTransaction(ref: ActorRef, transaction: Transaction)

case class BankTransactionResponse(ref: ActorRef, transaction: Transaction, account: Option[BankAccount])

case class MortgageBankTransactionResponse(ref: ActorRef, transaction: Transaction, account: Option[BankAccount], mortgage: Option[Mortgage])
