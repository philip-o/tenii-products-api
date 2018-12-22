package com.ogunleye.tenii.products.model.implicits

import com.ogunleye.tenii.products.model.api.{ SourceBankAccount => APIBankAccount }
import com.ogunleye.tenii.products.model.db.BankAccount

trait AccountImplicit {

  implicit def transformAPIAccountToDBAccount(account: APIBankAccount): BankAccount = {
    BankAccount(
      teniiId = account.teniiId,
      accountId = account.accountId
    )
  }

  implicit def transformDBAccountToAPIAccount(account: BankAccount): APIBankAccount = {
    APIBankAccount(
      account.teniiId,
      account.accountId
    )
  }
}
