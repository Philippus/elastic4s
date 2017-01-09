package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.elastic4s.searches.queries.QueryStringQueryDefinition
import com.sksamuel.elastic4s.{DocumentRef, ProxyClients}
import org.elasticsearch.action.explain.{ExplainAction, ExplainRequest, ExplainRequestBuilder}

trait ExplainDsl {

  def explain(ref: DocumentRef) = ExplainDefinition(ref.index, ref.`type`, ref.id)
  def explain(index: String, `type`: String, id: String) = ExplainDefinition(index, `type`, id)
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
