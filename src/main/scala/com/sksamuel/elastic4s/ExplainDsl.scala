package com.sksamuel.elastic4s

import org.elasticsearch.action.explain.ExplainRequestBuilder
import org.elasticsearch.action.support.QuerySourceBuilder
import com.sksamuel.elastic4s.DefinitionAttributes.{ DefinitionAttributePreference, DefinitionAttributeRouting }

/** @author Stephen Samuel */
trait ExplainDsl {

  def explain = new ExplainExpectsId
  class ExplainExpectsId {
    def id(id: Any) = new ExplainExpectsIndex(id)
  }
  class ExplainExpectsIndex(id: Any) {
    def in(indexesTypes: IndexesTypes): ExplainDefinition = new ExplainDefinition(indexesTypes, id)
  }

  class ExplainDefinition(indexesTypes: IndexesTypes, id: Any)
      extends DefinitionAttributeRouting
      with DefinitionAttributePreference {

    val _builder = new ExplainRequestBuilder(ProxyClients.client, indexesTypes.index, indexesTypes.typ.get, id.toString)

    def build = _builder.request

    def query(string: String): ExplainDefinition = {
      val q = new StringQueryDefinition(string)
      // need to set the query on the request - workaround for ES internals
      _builder.request.source(new QuerySourceBuilder().setQuery(q.builder))
      _builder.setQuery(q.builder.buildAsBytes)
      this
    }

    def query(block: => QueryDefinition): ExplainDefinition = {
      // need to set the query on the request - workaround for ES internals
      _builder.request.source(new QuerySourceBuilder().setQuery(block.builder))
      _builder.setQuery(block.builder)
      this
    }
  }
}
