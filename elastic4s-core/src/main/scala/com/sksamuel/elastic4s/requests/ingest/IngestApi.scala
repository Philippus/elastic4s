package com.sksamuel.elastic4s.requests.ingest

trait IngestApi {

  def putPipeline(id: String, description: String, processors: Seq[Processor]): PutPipelineRequest =
    PutPipelineRequest(id, description, processors)

}
