package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

trait SearchTemplateApi {

  def putSearchTemplate(name: String): PutSearchTemplateExpectsQueryOrBody = new PutSearchTemplateExpectsQueryOrBody(name)

  class PutSearchTemplateExpectsQueryOrBody(name: String) {
    def query(query: QueryDefinition): PutSearchTemplateDefinition = PutSearchTemplateDefinition(name, query.some, None)
    def body(body: String): PutSearchTemplateDefinition = PutSearchTemplateDefinition(name, None, body.some)
  }

  @deprecated("Use the stored scripts api to store templates", "6.0.0")
  def putSearchTemplate(name: String, query: QueryDefinition): PutSearchTemplateDefinition = PutSearchTemplateDefinition(name, query.some, None)

  @deprecated("Use the stored scripts api to store templates", "6.0.0")
  def putSearchTemplate(name: String, body: String): PutSearchTemplateDefinition = PutSearchTemplateDefinition(name, none, body.some)

  @deprecated("Use the stored scripts api to get templates", "6.0.0")
  def getSearchTemplate(name: String): GetSearchTemplateDefinition = GetSearchTemplateDefinition(name)

  @deprecated("Use the stored scripts api to delete templates", "6.0.0")
  def removeSearchTemplate(name: String): RemoveSearchTemplateDefinition = RemoveSearchTemplateDefinition(name)

  def templateSearch(indexesAndTypes: IndexesAndTypes): TemplateSearchExpectsName = new TemplateSearchExpectsName(indexesAndTypes)

  class TemplateSearchExpectsName(indexesAndTypes: IndexesAndTypes) {

    def name(name: String): TemplateSearchExpectsParams = new TemplateSearchExpectsParams(indexesAndTypes, name)

    class TemplateSearchExpectsParams(indexesAndTypes: IndexesAndTypes, name: String) {
      def params(params: Map[String, Any]): TemplateSearchDefinition = TemplateSearchDefinition(indexesAndTypes, name, params)
    }
  }
}

@deprecated("Use the stored scripts api", "6.0.0")
case class PutSearchTemplateDefinition(name: String, query: Option[QueryDefinition], body: Option[String])
case class GetSearchTemplateDefinition(name: String)

@deprecated("Use the stored scripts api", "6.0.0")
case class RemoveSearchTemplateDefinition(name: String)
case class TemplateSearchDefinition(indexesAndTypes: IndexesAndTypes, name: String, params: Map[String, Any])


