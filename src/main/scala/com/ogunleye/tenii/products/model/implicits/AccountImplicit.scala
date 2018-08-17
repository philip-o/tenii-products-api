package com.ogunleye.tenii.products.model.implicits

import com.ogunleye.tenii.products.model.api.{ BankAccount => APIBankAccount }
import com.ogunleye.tenii.products.model.db.BankAccount

trait AccountImplicit {

  implicit def transformAPIAccountToDBAccount(account: APIBankAccount): BankAccount = {
    BankAccount(
      userId = account.userId,
      provider = account.provider,
      sortCode = account.sortCode,
      accountNumber = account.accountNumber,
      balance = account.balance.toDouble
    )
  }

  implicit def transformDBAccountToAPIAccount(account: BankAccount): APIBankAccount = {
    APIBankAccount(
      account.userId,
      account.provider,
      account.sortCode,
      account.accountNumber,
      account.balance
    )
  }
}
