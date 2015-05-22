package com.sksamuel.elastic4s

import org.elasticsearch.action.count.{ CountRequestBuilder, CountResponse }
import org.elasticsearch.action.support.QuerySourceBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.index.query.{ QueryBuilder, QueryBuilders }

import scala.concurrent.Future

/** @author Stephen Samuel */
trait CountDsl {

  def count(indexesTypes: IndexesTypes): CountDefinition = new CountDefinition(indexesTypes)
  def count(indexes: String*): CountDefinition = count(IndexesTypes(indexes))

  implicit object CountDefinitionExecutable extends Executable[CountDefinition, CountResponse] {
    override def apply(client: Client, t: CountDefinition): Future[CountResponse] = injectFuture(client
      .count(t.build, _))
  }

}

class CountDefinition(indexesTypes: IndexesTypes) {

  val _builder = new CountRequestBuilder(ProxyClients.client)
    .setIndices(indexesTypes.indexes: _*)
    .setTypes(indexesTypes.types: _*)
    .setQuery(QueryBuilders.matchAllQuery())

  def build = _builder.request()

  def routing(routing: String): this.type = {
    _builder.setRouting(routing)
    this
  }

  def minScore(minScore: Double): this.type = {
    _builder.setMinScore(minScore.toFloat)
    this
  }

  def preference(pref: String): this.type = {
    _builder.setPreference(pref)
    this
  }

  /** The maximum count for each shard, upon reaching which the query execution will terminate early.
    * If set, the response will have a boolean field terminated_early to indicate whether the query execution
    * has actually terminated_early. Defaults to no terminate_after.
    */
  def terminateAfter(termAfter: Int): this.type = {
    _builder.setTerminateAfter(termAfter)
    this
  }

  def where(string: String): CountDefinition = query(new SimpleStringQueryDefinition(string))
  def where(block: => QueryDefinition): CountDefinition = javaquery(block.builder)
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
