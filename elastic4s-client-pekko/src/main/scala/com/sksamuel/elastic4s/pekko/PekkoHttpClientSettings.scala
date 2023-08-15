package com.sksamuel.elastic4s.pekko

import java.util.concurrent.TimeUnit

import org.apache.pekko.http.scaladsl.model.HttpRequest
import org.apache.pekko.http.scaladsl.settings.ConnectionPoolSettings
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.util.Try

object PekkoHttpClientSettings {

  private def defaultConfig: Config =
    ConfigFactory.load().getConfig("com.sksamuel.elastic4s.pekko")

  lazy val default: PekkoHttpClientSettings = apply(defaultConfig)

  def apply(config: Config): PekkoHttpClientSettings = {
    val cfg = config.withFallback(defaultConfig)
    val hosts = cfg.getStringList("hosts").asScala.toVector
    val username = Try(cfg.getString("username")).map(Some(_)).getOrElse(None)
    val password = Try(cfg.getString("password")).map(Some(_)).getOrElse(None)
    val queueSize = cfg.getInt("queue-size")
    val https = cfg.getBoolean("https")
    val verifySslCertificate = Try(cfg.getBoolean("verify-ssl-certificate")).toOption.getOrElse(true)
    val blacklistMinDuration = Duration(
      cfg.getDuration("blacklist.min-duration", TimeUnit.MILLISECONDS),
      TimeUnit.MILLISECONDS
    )
    val blacklistMaxDuration = Duration(
      cfg.getDuration("blacklist.max-duration", TimeUnit.MILLISECONDS),
      TimeUnit.MILLISECONDS
    )
    val maxRetryTimeout = Duration(
      cfg.getDuration("max-retry-timeout", TimeUnit.MILLISECONDS),
      TimeUnit.MILLISECONDS
    )
    val poolSettings = ConnectionPoolSettings(
      cfg.withFallback(ConfigFactory.load())
    )
    PekkoHttpClientSettings(
      https,
      hosts,
      username,
      password,
      queueSize,
      poolSettings,
      verifySslCertificate,
      blacklistMinDuration,
      blacklistMaxDuration,
      maxRetryTimeout
    )
  }

  def apply(): PekkoHttpClientSettings = {
    default
  }

  def apply(hosts: Seq[String]): PekkoHttpClientSettings = {
    apply().copy(hosts = hosts.toVector)
  }
}

case class PekkoHttpClientSettings(
  https: Boolean,
  hosts: Vector[String],
  username: Option[String],
  password: Option[String],
  queueSize: Int,
  poolSettings: ConnectionPoolSettings,
  verifySSLCertificate : Boolean,
  blacklistMinDuration: FiniteDuration =
    PekkoHttpClientSettings.default.blacklistMinDuration,
  blacklistMaxDuration: FiniteDuration =
    PekkoHttpClientSettings.default.blacklistMaxDuration,
  maxRetryTimeout: FiniteDuration =
    PekkoHttpClientSettings.default.maxRetryTimeout,
  requestCallback: HttpRequest => HttpRequest = identity
) {
  def hasCredentialsDefined: Boolean = username.isDefined && password.isDefined
}
