package com.sksamuel.elastic4s.handlers.pit

import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}
import com.sksamuel.elastic4s.requests.pit.{CreatePitRequest, CreatePitResponse, DeletePitRequest, DeletePitResponse}

trait PitHandlers {
  implicit object CreatePitHandler extends Handler[CreatePitRequest, CreatePitResponse] {

    override def build(request: CreatePitRequest): ElasticRequest =
      ElasticRequest(
        method = "POST",
        endpoint = s"/${request.index.name}/_pit",
        params = request.keepAlive
          .map(keepAlive => Map("keep_alive" -> s"${keepAlive.toSeconds}s"))
          .getOrElse(Map.empty[String, String])
      )
  }

  implicit object DeletePitHandler extends Handler[DeletePitRequest, DeletePitResponse] {

    override def build(request: DeletePitRequest): ElasticRequest = {
      val entity = HttpEntity(DeletePitBuilderFn(request).string, "application/json")
      ElasticRequest(
        method = "DELETE",
        endpoint = s"/_pit",
        entity
      )
    }
  }


}
