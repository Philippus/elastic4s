package com.sksamuel.elastic4s

import org.elasticsearch.action.explain.ExplainRequestBuilder
import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributePreference, DefinitionAttributeRouting}

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
    extends DefinitionAttributeRouting with DefinitionAttributePreference {
    val _builder = new ExplainRequestBuilder(null, indexesTypes.index, indexesTypes.typ.get, id.toString)
    def build = _builder.request

    def query(string: String): ExplainDefinition = {
      val q = new StringQueryDefinition(string)
      _builder.setQuery(q.builder.buildAsBytes)
      this
    }

    def query(block: => QueryDefinition): ExplainDefinition = {
      _builder.setQuery(block.builder)
      this
    }
  }
}
