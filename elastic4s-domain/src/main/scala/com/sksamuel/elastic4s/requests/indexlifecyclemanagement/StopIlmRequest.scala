package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import scala.concurrent.duration.Duration

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits

case class StopIlmRequest(masterTimeout: Option[String] = None, timeout: Option[String] = None) {
  def masterTimeout(timeout: Duration): StopIlmRequest = copy(masterTimeout = s"${timeout.toNanos}nanos".some)
  def masterTimeout(timeout: String): StopIlmRequest   = copy(masterTimeout = timeout.some)

  def timeout(timeout: Duration): StopIlmRequest = copy(timeout = s"${timeout.toNanos}nanos".some)
  def timeout(timeout: String): StopIlmRequest   = copy(timeout = timeout.some)
}
