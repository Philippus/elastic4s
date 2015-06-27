package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.definitions.{DefinitionTerminateAfter, DefinitionMinScore, DefinitionPreference, DefinitionRouting}
import org.elasticsearch.action.count.{CountRequestBuilder, CountResponse}
import org.elasticsearch.action.support.QuerySourceBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentHelper
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders}

import scala.concurrent.Future

/** @author Stephen Samuel */
trait CountDsl {

  implicit object CountDefinitionExecutable extends Executable[CountDefinition, CountResponse, CountResponse] {
    override def apply(client: Client, t: CountDefinition): Future[CountResponse] = {
      injectFuture(client.count(t.build, _))
    }
  }

  implicit object CountDefinitionShow extends Show[CountDefinition] {
    override def show(f: CountDefinition): String = XContentHelper.convertToJson(f.build.source, true, true)
  }

  implicit class CountDefinitionShowOps(f: CountDefinition) {
    def show: String = CountDefinitionShow.show(f)
  }
}

class CountDefinition(indexesTypes: IndexesTypes)
  extends DefinitionRouting
  with DefinitionPreference
  with DefinitionMinScore
  with DefinitionTerminateAfter {

  protected val _builder = new CountRequestBuilder(ProxyClients.client)
    .setIndices(indexesTypes.indexes: _*)
    .setTypes(indexesTypes.types: _*)
    .setQuery(QueryBuilders.matchAllQuery())

  def build = _builder.request()

  @deprecated("use query instead of where", "1.6.0")
  def where(string: String): CountDefinition = query(new SimpleStringQueryDefinition(string))
  @deprecated("use query instead of where", "1.6.0")
  def where(block: => QueryDefinition): CountDefinition = query(block)

  def query(string: String): CountDefinition = query(new SimpleStringQueryDefinition(string))
  def query(block: => QueryDefinition): CountDefinition = javaquery(block.builder)
  def javaquery(block: => QueryBuilder): CountDefinition = {
    _builder.request.source(new QuerySourceBuilder().setQuery(block))
    //_builder.setQuery(block)
    this
  }

  def types(iterable: Iterable[String]): CountDefinition = types(iterable.toSeq: _*)
  def types(types: String*): CountDefinition = {
    _builder.setTypes(types: _*)
    this
  }
}
