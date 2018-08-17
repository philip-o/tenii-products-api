package com.ogunleye.tenii.products.model.implicits

import com.ogunleye.tenii.products.model.api.{ Mortgage => APIMortgage }
import com.ogunleye.tenii.products.model.db.{ Mortgage => DBMortgage }

trait MortgageImplicit {

  implicit def transformAPIMortgageToDBMortgage(mortgage: APIMortgage): DBMortgage = {
    DBMortgage(
      userId = mortgage.userId,
      provider = mortgage.provider,
      mortgageType = mortgage.mortgageType,
      monthlyPayment = mortgage.monthlyPayment,
      accountNumber = mortgage.accountNumber,
      productStarted = mortgage.productStarted,
      rate = mortgage.rate,
      balanceAtYearStart = mortgage.balanceAtYearStart,
      dateFixedProductEnds = mortgage.dateFixedProductEnds,
      balance = mortgage.balance
    )
  }

  implicit def transformBDMortgageToAPIMortgage(mortgage: DBMortgage): APIMortgage = {
    APIMortgage(
      mortgage.userId,
      mortgage.provider,
      mortgage.mortgageType,
      mortgage.monthlyPayment,
      mortgage.accountNumber,
      mortgage.productStarted,
      mortgage.rate,
      mortgage.balanceAtYearStart,
      mortgage.dateFixedProductEnds,
      mortgage.balance
    )
  }
}
