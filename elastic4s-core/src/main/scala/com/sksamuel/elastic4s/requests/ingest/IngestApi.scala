package com.sksamuel.elastic4s.requests.ingest

trait IngestApi {

  def getPipeline(id: String): GetPipelineRequest = GetPipelineRequest(id)

  def putPipeline(id: String, description: String, processors: Seq[Processor]): PutPipelineRequest =
    PutPipelineRequest(id, description, processors)

  def deletePipeline(id: String): DeletePipelineRequest = DeletePipelineRequest(id)

}
