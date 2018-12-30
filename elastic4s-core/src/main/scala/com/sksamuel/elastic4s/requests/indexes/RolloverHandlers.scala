package com.sksamuel.elastic4s.requests.indexes

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.admin.RolloverIndexRequest
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity, XContentFactory}

case class RolloverResponse(@JsonProperty("old_index") oldIndex: String,
                            @JsonProperty("new_index") newIndex: String,
                            @JsonProperty("rolled_over") rolledOver: Boolean,
                            @JsonProperty("dry_run") dryRun: Boolean,
                            acknowledged: Boolean,
                            @JsonProperty("shards_acknowledged") shardsAcknowledged: Boolean,
                            conditions: Map[String, Boolean])

trait RolloverHandlers {

  implicit object RolloverHandler extends Handler[RolloverIndexRequest, RolloverResponse] {

    override def build(request: RolloverIndexRequest): ElasticRequest = {

      val endpoint  = s"/${request.sourceAlias}/_rollover"
      val endpoint2 = request.newIndexName.fold(endpoint)(endpoint + "/" + _)

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.dryRun.foreach(params.put("dry_run", _))

      val builder = XContentFactory.jsonBuilder()
      builder.startObject("conditions")
      request.maxAge.foreach(builder.field("max_age", _))
      request.maxDocs.foreach(builder.field("max_docs", _))
      request.maxSize.foreach(builder.field("max_size", _))
      builder.endObject()

      val entity = HttpEntity(builder.string, "application/json")
      ElasticRequest("POST", endpoint2, params.toMap, entity)
    }
  }
}
