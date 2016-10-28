package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.query.QueryStringQueryDefinition
import org.elasticsearch.action.explain.{ExplainAction, ExplainRequest, ExplainRequestBuilder, ExplainResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

/** @author Stephen Samuel */
trait ExplainDsl {

  def explain(index: String, `type`: String, id: String) = ExplainDefinition(index, `type`, id)

  implicit object ExplainDefinitionExecutable extends Executable[ExplainDefinition, ExplainResponse, ExplainResponse] {
    override def apply(c: Client, t: ExplainDefinition): Future[ExplainResponse] = {
      val builder = t.build(c.prepareExplain(t.index, t.`type`, t.id))
      injectFuture(builder.execute)
    }
  }
}

case class ExplainDefinition(index: String,
                             `type`: String,
                             id: String,
                             query: Option[QueryDefinition] = None,
                             fetchSource: Option[Boolean] = None,
                             parent: Option[String] = None,
                             preference: Option[String] = None,
                             routing: Option[String] = None) extends Serializable {

  // used by testing to get the full builder without a client
  private[elastic4s] def request: ExplainRequest = {
    build(new ExplainRequestBuilder(ProxyClients.client, ExplainAction.INSTANCE, index, `type`, id)).request()
  }

  def build(builder: ExplainRequestBuilder): ExplainRequestBuilder = {
    query.foreach(q => builder.setQuery(q.builder))
    fetchSource.foreach(builder.setFetchSource)
    parent.foreach(builder.setParent)
    preference.foreach(builder.setPreference)
    routing.foreach(builder.setRouting)
    builder
  }

  def query(string: String): ExplainDefinition = query(QueryStringQueryDefinition(string))
  def query(block: => QueryDefinition): ExplainDefinition = copy(query = Option(block))
  def fetchSource(fetchSource: Boolean): ExplainDefinition = copy(fetchSource = Option(fetchSource))
  def parent(parent: String): ExplainDefinition = copy(parent = Option(parent))
  def preference(preference: String): ExplainDefinition = copy(preference = Option(preference))
  def routing(routing: String): ExplainDefinition = copy(routing = Option(routing))
}
