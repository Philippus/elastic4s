package com.sksamuel.elastic4s.requests.ingest

case class PutPipelineRequest(id: String, description: String, processors: Seq[Processor] = Seq.empty)

object PutPipelineRequest {
  def apply(id: String, description: String, processor: Processor): PutPipelineRequest =
    PutPipelineRequest(id, description, Seq(processor))
}
