package com.ogunleye.tenii.products.helpers

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.ogunleye.tenii.products.model.db.BankAccount
import com.ogunleye.tenii.products.model.{ Roar, RoarType }
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike }

class TransactionHelperTest extends TestKit(ActorSystem("TransactionHelperTest")) with MockFactory with BeforeAndAfterAll with FlatSpecLike {

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  val userId = "userId1"
  val provider = "provider"
  val sortCode = "sortCode"
  val accountNumber = "accountNumber"
  val date = "DDMMYYYY"
  //val transaction = Transaction(userId, provider, sortCode, accountNumber, date, -5.34)
  val balancedRoarType = RoarType(Roar.BALANCED)
  val huntRoarType = RoarType(Roar.HUNT)
  val stripesRoarType = RoarType(Roar.STRIPES, Some(10))
  //val account = BankAccount(None, userId, provider, sortCode, accountNumber, 110)

//  "TransactionHelper" should "return true for a Balanced roar type, a balance of 110 and a debit transaction of 5.34" in {
//
//    val response = BankTransactionResponse(testActor, transaction, Some(account))
//    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(balancedRoarType, response))
//  }
//
//  it should "return false for a Balanced roar type, a balance of 5 and a debit transaction of 5.34" in {
//
//    val anotherAccount = account.copy(balance = 5)
//    val response = BankTransactionResponse(testActor, transaction, Some(anotherAccount))
//
//    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(balancedRoarType, response))
//  }
//
//  it should "return false for a Balanced roar type, a balance of -5 and a debit transaction of 1.34" in {
//
//    val anotherAccount = account.copy(balance = -5)
//    val anotherTransaction = transaction.copy(amount = -1.34)
//    val response = BankTransactionResponse(testActor, anotherTransaction, Some(anotherAccount))
//
//    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(balancedRoarType, response))
//  }
//
//  it should "return false for a Balanced roar type, a balance of 2.99 and a debit transaction of 3.34" in {
//
//    val anotherAccount = account.copy(balance = 2.99)
//    val anotherTransaction = transaction.copy(amount = -3.34)
//    val response = BankTransactionResponse(testActor, anotherTransaction, Some(anotherAccount))
//
//    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(balancedRoarType, response))
//  }
//
//  it should "return true for a Hunt roar type, a balance of 110 and a debit transaction of 5.34" in {
//
//    val response = BankTransactionResponse(testActor, transaction, Some(account))
//    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(huntRoarType, response))
//  }
//
//  it should "return false for a Hunt roar type, a balance of 5 and a debit transaction of 5.34" in {
//
//    val anotherAccount = account.copy(balance = 5)
//    val response = BankTransactionResponse(testActor, transaction, Some(anotherAccount))
//
//    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(huntRoarType, response))
//  }
//
//  it should "return true for a Hunt roar type, a balance of 7 and a debit transaction of 5.34" in {
//
//    val anotherAccount = account.copy(balance = 7)
//    val response = BankTransactionResponse(testActor, transaction, Some(anotherAccount))
//
//    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(huntRoarType, response))
//  }
//
//  it should "return false for a Stripes roar type with no limit" in {
//    val response = BankTransactionResponse(testActor, transaction, Some(account))
//
//    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(stripesRoarType.copy(limit = None), response))
//  }
//
//  it should "return true for a Stripes roar type, a limit of 10, a balance of 15 and a debit transaction of 5.34" in {
//    val anotherAccount = account.copy(balance = 15)
//    val response = BankTransactionResponse(testActor, transaction, Some(anotherAccount))
//
//    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(stripesRoarType, response))
//  }
//
//  it should "return true for a Stripes roar type, a limit of 20, a balance of 15 and a debit transaction of 5.34" in {
//    val anotherAccount = account.copy(balance = 15)
//    val response = BankTransactionResponse(testActor, transaction, Some(anotherAccount))
//
//    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(stripesRoarType.copy(limit = Some(20)), response))
//  }
//
//  it should "return false for a Stripes roar type, a limit of 10, a balance of 10 and a debit transaction of 10.34" in {
//    val anotherAccount = account.copy(balance = 10)
//    val response = BankTransactionResponse(testActor, transaction.copy(amount = -10.34), Some(anotherAccount))
//
//    assert(!TransactionHelper.checkIfRoarTypeAllowsRounding(stripesRoarType, response))
//  }
//
//  it should "return true for a Stripes roar type, a limit of 10, a balance of 20 and a debit transaction of 8.34" in {
//    val anotherAccount = account.copy(balance = 20)
//    val response = BankTransactionResponse(testActor, transaction.copy(amount = -8.34), Some(anotherAccount))
//
//    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(stripesRoarType, response))
//  }

  //  it should "return true for a Stripes roar type, a limit of 20, a balance of 50 and a debit transaction of 29.80" in {
  //    val anotherAccount = account.copy(balance = 50)
  //    val response = BankTransactionResponse(testActor, transaction.copy(amount = -29.80), Some(anotherAccount))
  //
  //    assert(TransactionHelper.checkIfRoarTypeAllowsRounding(stripesRoarType.copy(limit = Some(20)), response))
  //  }
}
