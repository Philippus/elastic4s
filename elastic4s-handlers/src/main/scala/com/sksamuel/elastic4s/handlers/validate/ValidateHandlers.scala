package com.sksamuel.elastic4s.handlers.validate

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.validate.{ValidateRequest, ValidateResponse}
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}

object ValidateBodyFn {
  def apply(v: ValidateRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", queries.QueryBuilderFn(v.query))
    builder.endObject()
  }
}

trait ValidateHandlers {

  implicit object ValidateHandler extends Handler[ValidateRequest, ValidateResponse] {

    override def build(request: ValidateRequest): ElasticRequest = {

      val endpoint = s"/${request.indexes.values.mkString(",")}/_validate/query"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.explain.map(_.toString).foreach(params.put("explain", _))
      request.rewrite.map(_.toString).foreach(params.put("rewrite", _))
      request.lenient.map(_.toString).foreach(params.put("lenient", _))
      request.analyzeWildcard.map(_.toString).foreach(params.put("analyze_wildcard", _))
      request.ignoreUnavailable.map(_.toString).foreach(params.put("ignore_unavailable", _))

      val body   = ValidateBodyFn(request).string
      val entity = HttpEntity(body, "application/json")

      ElasticRequest("GET", endpoint, params.toMap, entity)
    }
  }
}
