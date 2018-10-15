package com.ogunleye.tenii.products.helpers

import com.ogunleye.tenii.products.model.{ Roar, RoarType }
import com.ogunleye.tenii.products.model.db.BankAccount
import com.ogunleye.tenii.products.model.domain.BankTransactionResponse
import com.typesafe.scalalogging.LazyLogging

object TransactionHelper extends LazyLogging {

  def checkIfRoarTypeAllowsRounding(roarType: RoarType, response: BankTransactionResponse): Boolean = {

    val roundedTransaction = math.ceil(response.transaction.amount * -1)

    def checkTeniiAmountAgainstAccount(account: BankAccount, amount: Double): Boolean = {
      if (account.balance - roundedTransaction > 0)
        true
      else {
        logger.error(s"Amount of $roundedTransaction will push account into the overdraft so will not deduct Tenii amount $amount")
        false
      }
    }

    if (response.transaction.amount < 0) {
      roarType.roar match {
        case Roar.BALANCED =>
          val extra = BigDecimal(roundedTransaction + response.transaction.amount).setScale(2, BigDecimal.RoundingMode.UP).toDouble
          checkTeniiAmountAgainstAccount(response.account.get, extra)
        case Roar.HUNT =>
          val extra = BigDecimal(roundedTransaction + response.transaction.amount).setScale(2, BigDecimal.RoundingMode.UP).*(2).toDouble
          checkTeniiAmountAgainstAccount(response.account.get, extra)
        case Roar.STRIPES if roarType.limit.isDefined => processStripesSpend(response.transaction.amount, roarType.limit.get, response.account.get)
        case Roar.STRIPES if roarType.limit.isEmpty =>
          logger.error(s"User ${response.transaction.userId} has no limit but has stripes set, investigate and fix urgently")
          false
      }
    } else
      false
  }

  private def processStripesSpend(spent: Double, limit: Int, account: BankAccount): Boolean = limit + spent > 0
}
