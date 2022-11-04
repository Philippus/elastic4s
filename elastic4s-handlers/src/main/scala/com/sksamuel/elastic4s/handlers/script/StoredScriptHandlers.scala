package com.sksamuel.elastic4s.handlers.script

import com.sksamuel.elastic4s.requests.script.{
  DeleteStoredScriptRequest,
  DeleteStoredScriptResponse,
  GetStoredScriptRequest,
  GetStoredScriptResponse,
  PutStoredScriptRequest,
  PutStoredScriptResponse
}
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s._

trait StoredScriptHandlers {

  implicit object DeleteStoredScriptHandler extends Handler[DeleteStoredScriptRequest, DeleteStoredScriptResponse] {
    override def build(request: DeleteStoredScriptRequest): ElasticRequest = {
      val endpoint = "/_scripts/" + request.id
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object GetStoredScriptHandler extends Handler[GetStoredScriptRequest, GetStoredScriptResponse] {
    override def build(request: GetStoredScriptRequest): ElasticRequest = {
      val endpoint = "/_scripts/" + request.id
      ElasticRequest("GET", endpoint)
    }
  }

  implicit object PutStoredScriptHandler extends Handler[PutStoredScriptRequest, PutStoredScriptResponse] {
    override def build(request: PutStoredScriptRequest): ElasticRequest = {
      val endpoint = "/_scripts/" + request.id

      val builder = XContentFactory.jsonBuilder()
      builder.startObject("script")
      builder.field("lang", request.script.lang)
      builder.field("source", request.script.source)
      builder.endObject()

      ElasticRequest("PUT", endpoint, HttpEntity(builder.string))
    }
  }
}

object StoredScriptHandlers extends StoredScriptHandlers
