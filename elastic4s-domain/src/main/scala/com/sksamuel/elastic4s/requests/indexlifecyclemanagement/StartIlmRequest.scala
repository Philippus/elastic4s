package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import scala.concurrent.duration.Duration

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits

case class StartIlmRequest(masterTimeout: Option[String] = None, timeout: Option[String] = None) {
  def masterTimeout(timeout: Duration): StartIlmRequest = copy(masterTimeout = s"${timeout.toNanos}nanos".some)
  def masterTimeout(timeout: String): StartIlmRequest   = copy(masterTimeout = timeout.some)

  def timeout(timeout: Duration): StartIlmRequest = copy(timeout = s"${timeout.toNanos}nanos".some)
  def timeout(timeout: String): StartIlmRequest   = copy(timeout = timeout.some)
}
