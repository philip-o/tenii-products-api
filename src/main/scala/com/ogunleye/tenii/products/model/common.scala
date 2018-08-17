package com.ogunleye.tenii.products.model

import com.ogunleye.tenii.products.model.Roar.Roar

case class TeniiDate(date: String) {
  def day = date.substring(0, 2)
  def month = date.substring(2, 4)
  def year = date.substring(4)
}

case class RoarType(roar: Roar, limit: Option[Int] = None)

object Roar extends Enumeration {
  type Roar = Value
  val BALANCED = Value("Balanced")
  val HUNT = Value("Hunt")
  val STRIPES = Value("Stripes")
}