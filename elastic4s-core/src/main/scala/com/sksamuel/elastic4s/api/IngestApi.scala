package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.ingest.{DeletePipelineRequest, GetPipelineRequest, Processor, PutPipelineRequest}

trait IngestApi {

  def getPipeline(id: String): GetPipelineRequest = GetPipelineRequest(id)

  def putPipeline(
      id: String,
      description: String,
      processors: Seq[Processor],
      version: Option[Int] = None
  ): PutPipelineRequest =
    PutPipelineRequest(id, description, processors, version)

  def deletePipeline(id: String): DeletePipelineRequest = DeletePipelineRequest(id)

}
