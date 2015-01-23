package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes.{ DefinitionAttributePreference, DefinitionAttributeRouting }
import org.elasticsearch.action.explain.{ ExplainRequestBuilder, ExplainResponse }
import org.elasticsearch.action.support.QuerySourceBuilder
import org.elasticsearch.client.Client

import scala.concurrent.Future

/** @author Stephen Samuel */
trait ExplainDsl {
  implicit object ExplainDefinitionExecutable
      extends Executable[ExplainDefinition, ExplainResponse] {
    override def apply(c: Client, t: ExplainDefinition): Future[ExplainResponse] = {
      injectFuture(c.explain(t.build, _))
    }
  }

  class ExplainExpectsIndex(id: Any) {
    def in(indexesTypes: IndexesTypes): ExplainDefinition = new ExplainDefinition(indexesTypes, id)
  }
}

class ExplainDefinition(indexesTypes: IndexesTypes, id: Any)
    extends DefinitionAttributeRouting
    with DefinitionAttributePreference {

  val _builder = new ExplainRequestBuilder(ProxyClients.client, indexesTypes.index, indexesTypes.typ.get, id.toString)

  def build = _builder.request

  def query(string: String): this.type = {
    val q = new QueryStringQueryDefinition(string)
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
