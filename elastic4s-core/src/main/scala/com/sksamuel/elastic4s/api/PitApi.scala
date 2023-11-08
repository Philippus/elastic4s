package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.requests.pit.{CreatePitRequest, DeletePitRequest}

trait PitApi {
  def createPointInTime(index: Index): CreatePitRequest = CreatePitRequest(index)

  def deletePointInTime(id: String) : DeletePitRequest  = DeletePitRequest(id)
}
