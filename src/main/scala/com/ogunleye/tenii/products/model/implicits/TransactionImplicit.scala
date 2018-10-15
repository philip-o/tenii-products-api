package com.ogunleye.tenii.products.model.implicits

import com.ogunleye.tenii.products.model.api.teller.{TellerTeniiPotCreditRequest, TellerTransaction}
import com.ogunleye.tenii.products.model.api.temp.{Transaction => APITransaction}
import com.ogunleye.tenii.products.model.db.{TellerDebitTransaction, Transaction}

trait TransactionImplicit {

  implicit def transformAPITransactionToDBTransaction(trans: APITransaction): Transaction = {
    Transaction(
      userId = trans.userId,
      provider = trans.provider,
      sortCode = trans.sortCode,
      accountNumber = trans.accountNumber,
      date = trans.date,
      amount = trans.amount
    )
  }

  implicit def toTellerDebitTransaction(trans: TellerTransaction, account: String): TellerDebitTransaction = {
    TellerDebitTransaction(
      userId = "",
      tellerAccountId = account,
      tellerTransactionId = trans.id,
      amount = trans.amount.toDouble,
      date = trans.date
    )
  }

  implicit def toTellerTeniiPotCreditRequest(trans: TellerTransaction, account: String) : TellerTeniiPotCreditRequest = {
    TellerTeniiPotCreditRequest(
      amount = trans.amount.toDouble,
      accountId = account
    )
  }
}
