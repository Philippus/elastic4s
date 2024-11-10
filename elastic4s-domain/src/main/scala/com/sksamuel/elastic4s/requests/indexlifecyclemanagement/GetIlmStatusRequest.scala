package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import scala.concurrent.duration.Duration

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits

case class GetIlmStatusRequest(masterTimeout: Option[String] = None, timeout: Option[String] = None) {
  def masterTimeout(timeout: Duration): GetIlmStatusRequest = copy(masterTimeout = s"${timeout.toNanos}nanos".some)
  def masterTimeout(timeout: String): GetIlmStatusRequest = copy(masterTimeout = timeout.some)

  def timeout(timeout: Duration): GetIlmStatusRequest = copy(timeout = s"${timeout.toNanos}nanos".some)
  def timeout(timeout: String): GetIlmStatusRequest = copy(timeout = timeout.some)
}
