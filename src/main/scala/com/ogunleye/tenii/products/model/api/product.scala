package com.ogunleye.tenii.products.model.api

import com.ogunleye.tenii.products.model.TeniiDate

trait TeniiProduct

case class Mortgage(userId: String, provider: Option[String] = None, mortgageType: String, monthlyPayment: Double, accountNumber: Option[Int] = None,
  productStarted: String, rate: Double, balanceAtYearStart: Option[Double], dateFixedProductEnds: Option[String], balance: Double) extends TeniiProduct

case class AddMortgageResponse(userId: String, cause: Option[String] = None)

case class GetMortgageRequest(userId: String)

case class GetMortgageResponse(mortgage: Option[Mortgage], cause: Option[String] = None)

case class StudentLoan() extends TeniiProduct

case class BankAccount(userId: String, provider: String, sortCode: String, accountNumber: String, balance: BigDecimal) extends TeniiProduct

case class AddBankAccountResponse(userId: String, cause: Option[String] = None)

case class GetBankAccountRequest(userId: String)

case class GetBankAccountResponse(account: Option[BankAccount], cause: Option[String] = None)

case class GetMultipleBankAccountsRequest(userId: String)

case class GetMultipleBankAccountsResponse(account: Option[List[BankAccount]], cause: Option[String] = None)

case class CreditCard() extends TeniiProduct

case class Pension() extends TeniiProduct
