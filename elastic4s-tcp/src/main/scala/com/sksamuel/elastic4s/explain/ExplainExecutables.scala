package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.action.explain.{ExplainRequestBuilder, ExplainResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ExplainExecutables {
  implicit object ExplainDefinitionExecutable extends Executable[ExplainDefinition, ExplainResponse, ExplainResponse] {

    def builder(c: Client, t: ExplainDefinition): ExplainRequestBuilder = {

      val _builder = c.prepareExplain(t.indexAndType.index, t.indexAndType.`type`, t.id)
      t.query.foreach(q => _builder.setQuery(QueryBuilderFn(q)))
      t.fetchSource.foreach(_builder.setFetchSource)
      t.parent.foreach(_builder.setParent)
      t.preference.foreach(_builder.setPreference)
      t.routing.foreach(_builder.setRouting)
      _builder
    }

    override def apply(c: Client, t: ExplainDefinition): Future[ExplainResponse] = {
      val _builder = builder(c, t)
      injectFuture(_builder.execute)
    }
  }
}
