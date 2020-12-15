package com.sksamuel.elastic4s.requests.ingest

trait IngestApi {

  def getPipeline(id: String): GetPipelineRequest = GetPipelineRequest(id)

  def putPipeline(id: String, description: String, processors: Seq[Processor], version: Option[Int] = None): PutPipelineRequest =
    PutPipelineRequest(id, description, processors, version)

  def deletePipeline(id: String): DeletePipelineRequest = DeletePipelineRequest(id)

}
