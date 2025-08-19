package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicy
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement._

trait IndexLifecycleManagementApi {
  def getIlmStatus: GetIlmStatusRequest = GetIlmStatusRequest()

  def startIlm(): StartIlmRequest = StartIlmRequest()

  def stopIlm(): StopIlmRequest = StopIlmRequest()

  def createIndexLifecyclePolicy(policy: IndexLifecyclePolicy): CreateLifecyclePolicyRequest =
    CreateLifecyclePolicyRequest(policy)

  def getIndexLifecyclePolicy(policyName: String): GetIndexLifecyclePolicyRequest =
    GetIndexLifecyclePolicyRequest(policyName)

  def deleteIndexLifecyclePolicy(policyName: String): DeleteIndexLifecyclePolicyRequest =
    DeleteIndexLifecyclePolicyRequest(policyName)
}
