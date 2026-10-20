package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import scala.concurrent.duration.Duration

case class GetIndexLifecyclePolicyRequest(
    policyName: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
) {
  def masterTimeout(timeout: Duration): GetIndexLifecyclePolicyRequest =
    copy(masterTimeout = Some(s"${timeout.toNanos}nanos"))
  def masterTimeout(timeout: String): GetIndexLifecyclePolicyRequest   = copy(masterTimeout = Some(timeout))

  def timeout(timeout: Duration): GetIndexLifecyclePolicyRequest = copy(timeout = Some(s"${timeout.toNanos}nanos"))
  def timeout(timeout: String): GetIndexLifecyclePolicyRequest   = copy(timeout = Some(timeout))
}
