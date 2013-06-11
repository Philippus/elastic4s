package com.sksamuel.elastic4s

/** @author Stephen Samuel */
case class SearchReq(query: Query,
                     timeout: Long,
                     from: Long,
                     size: Long,
                     routing: Option[String],
                     searchType: SearchType = SearchType.QueryThenFetch)
case class SearchResp(hits: Hits)

case class Hits(total: Long, hits: Seq[Hit])
case class Hit(index: String, id: String, `type`: String, source: Option[String] = None)

trait Query
case class StringQuery(queryString: String) extends Query
case class DslQuery extends Query

sealed trait SearchType
case object SearchType {
    case object DfsQueryThenFetch extends SearchType
    case object QueryThenFetch extends SearchType
    case object DfsQueryAndFetch extends SearchType
    case object QueryAndFetch extends SearchType
    case object Scan extends SearchType
    case object Count extends SearchType
}

case class TermQuery(field: String, value: String, boost: Double = 0)
case class BoolQueryBuilder(must: Seq[Any], mustNot: Seq[Any], should: Seq[Any], minimum_number_should_match: Int = 0, boost: Double = 0)