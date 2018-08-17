package com.ogunleye.tenii.products.helpers

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.ogunleye.tenii.products.model.api.temp.Transaction
import com.ogunleye.tenii.products.model.db.BankAccount
import com.ogunleye.tenii.products.model.domain.BankTransactionResponse
import com.ogunleye.tenii.products.model.{Roar, RoarType}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}

class TransactionHelperTest extends TestKit(ActorSystem("TransactionHelperTest")) with MockFactory with BeforeAndAfterAll with FlatSpecLike {

  override def afterAll()  {
    TestKit.shutdownActorSystem(system)
  }

  val userId = "userId1"
  val provider = "provider"
  val sortCode = "sortCode"
  val accountNumber = "accountNumber"
  val date = "DDMMYYYY"
  val transaction = Transaction(userId, provider, sortCode, accountNumber, date, -5.34)
  val balancedRoarType = RoarType(Roar.BALANCED)
  val huntRoarType = RoarType(Roar.HUNT)
  val account = BankAccount(None, userId, provider, sortCode, accountNumber, 110)

  "TransactionHelper" should "return true for a Balanced roar type, a balance of 110 and a transaction of 5.34" in {

    val response = BankTransactionResponse(testActor, transaction, Some(account))
    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(balancedRoarType, response))
  }

  it should "return false for a Balanced roar type, a balance of 5 and a transaction of 5.34" in {

    val anotherAccount = account.copy(balance = 5)
    val response = BankTransactionResponse(testActor, transaction, Some(anotherAccount))

    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(balancedRoarType, response))
  }

  it should "return false for a Balanced roar type, a balance of -5 and a transaction of 1.34" in {

    val anotherAccount = account.copy(balance = -5)
    val anotherTransaction = transaction.copy(amount = -1.34)
    val response = BankTransactionResponse(testActor, anotherTransaction, Some(anotherAccount))

    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(balancedRoarType, response))
  }

  it should "return false for a Balanced roar type, a balance of 2.99 and a transaction of 3.34" in {

    val anotherAccount = account.copy(balance = 2.99)
    val anotherTransaction = transaction.copy(amount = -3.34)
    val response = BankTransactionResponse(testActor, anotherTransaction, Some(anotherAccount))

    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(balancedRoarType, response))
  }

  it should "return true for a Hunt roar type, a balance of 110 and a transaction of 5.34" in {

    val response = BankTransactionResponse(testActor, transaction, Some(account))
    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(huntRoarType, response))
  }

  it should "return false for a Hunt roar type, a balance of 5 and a transaction of 5.34" in {

    val anotherAccount = account.copy(balance = 5)
    val response = BankTransactionResponse(testActor, transaction, Some(anotherAccount))

    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(huntRoarType, response))
  }

  it should "return true for a Hunt roar type, a balance of 7 and a transaction of 5.34" in {

    val anotherAccount = account.copy(balance = 7)
    val response = BankTransactionResponse(testActor, transaction, Some(anotherAccount))

    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(huntRoarType, response))
  }
}
