package com.sksamuel.elastic4s.requests.count

import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}

import java.net.URLEncoder

trait CountHandlers {
  implicit object CountHandler extends Handler[CountRequest, CountResponse] {

    override def build(request: CountRequest): ElasticRequest = {

      val endpoint = if (request.indexes.isEmpty)
        "/_all/_count"
      else
        "/" + request.indexes.values.map(URLEncoder.encode).mkString(",") + "/_count"

      val builder = CountBodyBuilderFn(request)
      val body = builder.string()

      val params = scala.collection.mutable.Map.empty[String, String]
      request.allowNoIndices.map(_.toString).foreach(params.put("allow_no_indices", _))
      request.ignoreUnavailable.map(_.toString).foreach(params.put("ignore_unavailable", _))
      request.ignoreThrottled.map(_.toString).foreach(params.put("ignore_throttled", _))
      request.analyzeWildcard.map(_.toString).foreach(params.put("analyze_wildcard", _))
      request.expandWildcards.foreach(params.put("expand_wildcards", _))
      request.lenient.map(_.toString).foreach(params.put("lenient", _))
      request.terminateAfter.map(_.toString).foreach(params.put("terminate_after", _))
      request.minScore.map(_.toString).foreach(params.put("min_score", _))

      ElasticRequest("GET", endpoint, params.toMap, HttpEntity(body, "application/json"))
    }
  }
}
