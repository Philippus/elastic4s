package com.sksamuel.elastic4s

/** @author Stephen Samuel */
abstract class SearchType(val elasticType: org.elasticsearch.action.search.SearchType)
case object SearchType {
  case object DfsQueryThenFetch extends SearchType(org.elasticsearch.action.search.SearchType.DFS_QUERY_THEN_FETCH)
  case object QueryThenFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_THEN_FETCH)
  case object DfsQueryAndFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_AND_FETCH)
  case object QueryAndFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_AND_FETCH)
  @deprecated("All benefits from this search type can now be achieved by doing a scroll request that sorts documents in _doc order", "2.1.0")
  case object Scan extends SearchType(org.elasticsearch.action.search.SearchType.SCAN)
  @deprecated("Does not have any improvement compared to #QUERY_THEN_FETCH with a `size` of  0", "2.0.0")
  case object Count extends SearchType(org.elasticsearch.action.search.SearchType.COUNT)
}
