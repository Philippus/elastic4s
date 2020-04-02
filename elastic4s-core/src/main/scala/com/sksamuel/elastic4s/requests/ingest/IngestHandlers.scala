package com.sksamuel.elastic4s.requests.ingest

import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity, XContentBuilder, XContentFactory}

trait IngestHandlers {

  implicit object PutPipelineRequestHandler extends Handler[PutPipelineRequest, PutPipelineResponse] {
    private def processorToXContent(p: Processor): XContentBuilder = {
      val xcb = XContentFactory.jsonBuilder()
      xcb.rawField(p.name, p.buildProcessorBody())
      xcb
    }

    override def build(request: PutPipelineRequest): ElasticRequest = {
      val xcb = XContentFactory.jsonBuilder()
      xcb.field("description", request.description)
      xcb.array("processors", request.processors.map(processorToXContent).toArray)
      xcb.endObject()
      ElasticRequest("PUT", s"_ingest/pipeline/${request.id}", HttpEntity(xcb.string()))
    }
  }

}
