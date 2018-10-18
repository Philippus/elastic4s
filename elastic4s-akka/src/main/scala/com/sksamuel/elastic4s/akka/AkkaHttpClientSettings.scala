package com.sksamuel.elastic4s.akka

import scala.collection.JavaConverters._

import akka.http.scaladsl.settings.ConnectionPoolSettings
import com.typesafe.config.{Config, ConfigFactory}

object AkkaHttpClientSettings {

  def apply(): AkkaHttpClientSettings = {
    apply(ConfigFactory.load().getConfig("com.sksamuel.elastic4s.akka"))
  }

  def apply(config: Config): AkkaHttpClientSettings = {
    val hosts = config.getStringList("hosts").asScala
    val queueSize = config.getInt("queue-size")
    val https = config.getBoolean("https")
    val poolSettings = ConnectionPoolSettings(config.withFallback(ConfigFactory.load()))
    AkkaHttpClientSettings(https, hosts, queueSize, poolSettings)
  }

  def apply(hosts: Seq[String]): AkkaHttpClientSettings = {
    apply().copy(hosts = hosts)
  }
}

case class AkkaHttpClientSettings(https: Boolean,
                                  hosts: Seq[String],
                                  queueSize: Int,
                                  poolSettings: ConnectionPoolSettings)
