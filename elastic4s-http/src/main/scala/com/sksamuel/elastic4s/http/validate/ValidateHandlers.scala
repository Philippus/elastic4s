package com.sksamuel.elastic4s.http.validate

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.Show
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.validate.ValidateRequest
import org.apache.http.entity.ContentType

case class ValidateResponse(valid: Boolean, @JsonProperty("_shards") shards: Shards, explanations: Seq[Explanation]) {
  def isValid: Boolean = valid
}

case class Explanation(index: String, valid: Boolean, error: String)

object ValidateBodyFn {
  def apply(v: ValidateRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", QueryBuilderFn(v.query))
    builder.endObject()
  }
}

trait ValidateHandlers {

  implicit object ValidateHandler extends Handler[ValidateRequest, ValidateResponse] {

    override def requestHandler(request: ValidateRequest): ElasticRequest = {

      val endpoint =
        s"${request.indexesAndTypes.indexes.mkString(",")}/${request.indexesAndTypes.types.mkString(",")}/_validate/query"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.explain.map(_.toString).foreach(params.put("explain", _))
      request.rewrite.map(_.toString).foreach(params.put("rewrite", _))

      val body   = ValidateBodyFn(request).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)

      ElasticRequest("GET", endpoint, params.toMap, entity)
    }
  }
}
