package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicy

import scala.concurrent.duration.Duration

case class CreateLifecyclePolicyRequest(
    policy: IndexLifecyclePolicy,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
) {
  def masterTimeout(timeout: Duration): CreateLifecyclePolicyRequest =
    copy(masterTimeout = s"${timeout.toNanos}nanos".some)
  def masterTimeout(timeout: String): CreateLifecyclePolicyRequest   = copy(masterTimeout = timeout.some)

  def timeout(timeout: Duration): CreateLifecyclePolicyRequest = copy(timeout = s"${timeout.toNanos}nanos".some)
  def timeout(timeout: String): CreateLifecyclePolicyRequest   = copy(timeout = timeout.some)

  def policy(policy: IndexLifecyclePolicy): CreateLifecyclePolicyRequest = copy(policy = policy)
}
