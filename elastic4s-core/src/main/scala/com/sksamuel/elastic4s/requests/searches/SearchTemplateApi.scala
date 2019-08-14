package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

trait SearchTemplateApi {

  def putSearchTemplate(name: String): PutSearchTemplateExpectsQueryOrBody =
    new PutSearchTemplateExpectsQueryOrBody(name)

  class PutSearchTemplateExpectsQueryOrBody(name: String) {
    def query(query: Query): PutSearchTemplateRequest = PutSearchTemplateRequest(name, query.some, None)
    def body(body: String): PutSearchTemplateRequest  = PutSearchTemplateRequest(name, None, body.some)
  }

  def templateSearch(indexesAndTypes: IndexesAndTypes): TemplateSearchExpectsName =
    new TemplateSearchExpectsName(indexesAndTypes)

  class TemplateSearchExpectsName(indexesAndTypes: IndexesAndTypes) {

    def name(name: String): TemplateSearchExpectsParams = new TemplateSearchExpectsParams(indexesAndTypes, name)

    class TemplateSearchExpectsParams(indexesAndTypes: IndexesAndTypes, name: String) {
      def params(params: Map[String, Any]): TemplateSearchRequest =
        TemplateSearchRequest(indexesAndTypes, name, params)
    }
  }
}

@deprecated("Use the stored scripts api", "6.0.0")
case class PutSearchTemplateRequest(name: String, query: Option[Query], body: Option[String])
case class GetSearchTemplateRequest(name: String)

@deprecated("Use the stored scripts api", "6.0.0")
case class RemoveSearchTemplateRequest(name: String)
case class TemplateSearchRequest(indexesAndTypes: IndexesAndTypes, name: String, params: Map[String, Any])
