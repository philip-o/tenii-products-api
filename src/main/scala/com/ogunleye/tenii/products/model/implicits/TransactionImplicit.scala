package com.ogunleye.tenii.products.model.implicits

import com.ogunleye.tenii.products.model.api.{TeniiPotCreditRequest, Transaction => APITransaction}
import com.ogunleye.tenii.products.model.db.DaysTransactions

trait TransactionImplicit {

  implicit def transformAPITransactionToDBTransaction(trans: APITransaction): DaysTransactions = {
    DaysTransactions(
      transactionIds = List(trans.transactionId),
      accountId = trans.accountId,
      teniiId = trans.teniiId,
      date = trans.date,
      amount = trans.amount
    )
  }

  def toPotCreditRequest(teniiId: String, amount: Double) = {
    TeniiPotCreditRequest(
      teniiId,
      amount
    )
  }
}
