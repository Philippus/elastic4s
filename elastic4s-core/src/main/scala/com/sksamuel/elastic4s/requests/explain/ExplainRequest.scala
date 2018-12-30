package com.sksamuel.elastic4s.requests.explain

import com.sksamuel.elastic4s.IndexAndType
import com.sksamuel.elastic4s.requests.searches.queries.{Query, QueryStringQuery}

case class ExplainRequest(indexAndType: IndexAndType,
                          id: String,
                          query: Option[Query] = None,
                          fetchSource: Option[Boolean] = None,
                          parent: Option[String] = None,
                          lenient: Option[Boolean] = None,
                          preference: Option[String] = None,
                          routing: Option[String] = None)
    extends Serializable {

  def query(string: String): ExplainRequest             = query(QueryStringQuery(string))
  def query(block: => Query): ExplainRequest            = copy(query = Option(block))
  def fetchSource(fetchSource: Boolean): ExplainRequest = copy(fetchSource = Option(fetchSource))
  def lenient(lenient: Boolean): ExplainRequest         = copy(lenient = Option(lenient))
  def parent(parent: String): ExplainRequest            = copy(parent = Option(parent))
  def preference(preference: String): ExplainRequest    = copy(preference = Option(preference))
  def routing(routing: String): ExplainRequest          = copy(routing = Option(routing))
}
