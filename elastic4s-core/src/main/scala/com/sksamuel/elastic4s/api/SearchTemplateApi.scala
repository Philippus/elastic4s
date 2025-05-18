package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.requests.searches.{PutSearchTemplateRequest, TemplateSearchRequest}
import com.sksamuel.elastic4s.ext.OptionImplicits._

trait SearchTemplateApi {
  def putSearchTemplate(name: String): PutSearchTemplateExpectsQueryOrBody =
    new PutSearchTemplateExpectsQueryOrBody(name)

  class PutSearchTemplateExpectsQueryOrBody(name: String) {
    def query(query: Query): PutSearchTemplateRequest = PutSearchTemplateRequest(name, query.some, None)
    def body(body: String): PutSearchTemplateRequest  = PutSearchTemplateRequest(name, None, body.some)
  }

  def templateSearch(indexes: Indexes): TemplateSearchExpectsName = new TemplateSearchExpectsName(indexes)

  class TemplateSearchExpectsName(indexes: Indexes) {
    def name(name: String): TemplateSearchExpectsParams = new TemplateSearchExpectsParams(indexes, name)

    class TemplateSearchExpectsParams(indexes: Indexes, name: String) {
      def params(params: Map[String, Any]): TemplateSearchRequest =
        TemplateSearchRequest(indexes, name, params)
    }
  }
}
