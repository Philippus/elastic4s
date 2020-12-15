package com.sksamuel.elastic4s.requests.ingest

case class GetPipelineResponse(id: String, description: String, version: Option[Int], processors: Seq[Processor])
