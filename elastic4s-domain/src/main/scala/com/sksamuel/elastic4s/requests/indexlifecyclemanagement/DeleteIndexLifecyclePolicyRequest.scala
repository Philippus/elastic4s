package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import scala.concurrent.duration.Duration

case class DeleteIndexLifecyclePolicyRequest(
    policyName: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
) {
  def masterTimeout(timeout: Duration): DeleteIndexLifecyclePolicyRequest =
    copy(masterTimeout = Some(s"${timeout.toNanos}nanos"))
  def masterTimeout(timeout: String): DeleteIndexLifecyclePolicyRequest   =
    copy(masterTimeout = Some(timeout))

  def timeout(timeout: Duration): DeleteIndexLifecyclePolicyRequest = copy(timeout = Some(s"${timeout.toNanos}nanos"))
  def timeout(timeout: String): DeleteIndexLifecyclePolicyRequest   = copy(timeout = Some(timeout))
}
