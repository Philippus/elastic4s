package com.sksamuel.elastic4s

/** @author Stephen Samuel */
case class SearchReq(query: Query,
                     filter: Option[Filter] = None,
                     timeout: Long = 0,
                     from: Long = 0,
                     size: Long = 0,
                     explain: Boolean = false,
                     sort: Option[Sort] = None,
                     routing: Option[String],
                     version: Boolean = false,
                     minScore: Double = 0,
                     fields: Seq[String] = Nil,
                     searchType: SearchType = SearchType.QueryThenFetch)
case class SearchResp(hits: Hits, facets: Seq[Facet])

case class Hits(total: Long, hits: Seq[Hit])
case class Hit(index: String, id: String, `type`: String, source: Option[String] = None)

case class Sort
case class Highlight
case class Facet
case class FacetTerm
case class FacetTermValue
case class IndexBoost

trait Filter
trait Query

sealed trait SearchType
case object SearchType {
    case object DfsQueryThenFetch extends SearchType
    case object QueryThenFetch extends SearchType
    case object DfsQueryAndFetch extends SearchType
    case object QueryAndFetch extends SearchType
    case object Scan extends SearchType
    case object Count extends SearchType
}

case class StringQuery(queryString: String,
                       defaultField: Option[String] = None,
                       defaultOperator: String = "AND",
                       analyzer: Option[String] = None,
                       allow_leading_wildcard: Boolean = true,
                       lowercase_expanded_terms: Boolean = true,
                       enable_position_increments: Boolean = true,
                       boost: Double = 0,
                       fuzzy_max_expansions: Int = 50,
                       fuzzy_min_sim: Double = 0.5,
                       fuzzy_prefix_length: Int = 0,
                       phrase_slop: Int = 0,
                       lenient: Boolean = true,
                       minimumShouldMatch: Int = 0,
                       exists: Option[String] = None,
                       missing: Option[String] = None) extends Query
case class FieldQuery(field: String, value: String, boost: Double = 0, enablePositionIncrements: Boolean) extends Query
case class TermQuery(field: String, value: String, boost: Double = 0) extends Query
case class TermsQuery(field: String, values: Seq[String], boost: Double = 0) extends Query
case class BoolQuery(must: Seq[Any], mustNot: Seq[Any], should: Seq[Any], minimumShouldMatch: Int = 0, boost: Double = 0)
  extends Query
case class PrefixQuery(field: String, prefix: String, boost: Double = 0) extends Query
case class BoostingQuery(positive: Query, positiveBoost: Double, negative: Query, negativeBoost: Double) extends Query
case class RegexQuery(field: String, regex: String, boost: Double = 0, flags: Seq[Int]) extends Query