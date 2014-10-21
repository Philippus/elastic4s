package com.sksamuel.elastic4s

import org.elasticsearch.action.explain.ExplainRequestBuilder
import org.elasticsearch.action.support.QuerySourceBuilder
import com.sksamuel.elastic4s.DefinitionAttributes.{ DefinitionAttributePreference, DefinitionAttributeRouting }

/** @author Stephen Samuel */
trait ExplainDsl {

  case object explain {
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

    def query(string: String): this.type = {
      val q = new StringQueryDefinition(string)
      // need to set the query on the request - workaround for ES internals
      _builder.request.source(new QuerySourceBuilder().setQuery(q.builder))
      _builder.setQuery(q.builder.buildAsBytes)
      this
    }

    def query(block: => QueryDefinition): this.type = {
      // need to set the query on the request - workaround for ES internals
      _builder.request.source(new QuerySourceBuilder().setQuery(block.builder))
      _builder.setQuery(block.builder)
      this
    }

    def fetchSource(fetchSource: Boolean): this.type = {
      _builder.setFetchSource(fetchSource)
      this
    }

    def parent(parent: String): this.type = {
      _builder.setParent(parent)
      this
    }
  }
}
