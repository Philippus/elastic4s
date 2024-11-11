package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.{StartIlmRequest, GetIlmStatusRequest, StopIlmRequest}

trait IndexLifecycleManagementApi {
  def getIlmStatus: GetIlmStatusRequest = GetIlmStatusRequest()

  def startIlm(): StartIlmRequest = StartIlmRequest()

  def stopIlm(): StopIlmRequest = StopIlmRequest()
}
