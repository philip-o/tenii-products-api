package com.ogunleye.tenii.products.config

import com.typesafe.config.{Config, ConfigFactory}

object Config {

  private[config] val config: Config = ConfigFactory.load()

  val database = config.getStringList("mongo.database").get(0)
  val host = config.getStringList("mongo.host").get(0)

  val teniiPayments = config.getStringList("tenii.payments.endpoint").get(0)
  val creditPath = config.getStringList("tenii.pathments.creditPath").get(0)

}
