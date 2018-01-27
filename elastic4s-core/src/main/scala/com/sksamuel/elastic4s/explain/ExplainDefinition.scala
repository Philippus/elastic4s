package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.IndexAndType
import com.sksamuel.elastic4s.searches.queries.{QueryDefinition, QueryStringQueryDefinition}

case class ExplainDefinition(indexAndType: IndexAndType,
                             id: String,
                             query: Option[QueryDefinition] = None,
                             fetchSource: Option[Boolean] = None,
                             parent: Option[String] = None,
                             lenient: Option[Boolean] = None,
                             preference: Option[String] = None,
                             routing: Option[String] = None)
    extends Serializable {

  def query(string: String): ExplainDefinition             = query(QueryStringQueryDefinition(string))
  def query(block: => QueryDefinition): ExplainDefinition  = copy(query = Option(block))
  def fetchSource(fetchSource: Boolean): ExplainDefinition = copy(fetchSource = Option(fetchSource))
  def lenient(lenient: Boolean): ExplainDefinition         = copy(lenient = Option(lenient))
  def parent(parent: String): ExplainDefinition            = copy(parent = Option(parent))
  def preference(preference: String): ExplainDefinition    = copy(preference = Option(preference))
  def routing(routing: String): ExplainDefinition          = copy(routing = Option(routing))
}
