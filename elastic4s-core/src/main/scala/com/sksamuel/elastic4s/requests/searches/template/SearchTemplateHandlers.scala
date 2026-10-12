package com.sksamuel.elastic4s.requests.searches.template

import com.sksamuel.elastic4s.requests.searches.{SearchResponse, TemplateSearchRequest}
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity, Indexes}

trait SearchTemplateHandlers {

  implicit object TemplateSearchHandler extends Handler[TemplateSearchRequest, SearchResponse] {

    override def build(req: TemplateSearchRequest): ElasticRequest = {
      val endpoint = req.indexes match {
        case Indexes(Nil)     => "/_search/template"
        case Indexes(indexes) => "/" + indexes.mkString(",") + "/_search/template"
      }
      val body     = TemplateSearchBuilderFn(req).string
      ElasticRequest("POST", endpoint, HttpEntity(body, "application/json"))
    }
  }
}
