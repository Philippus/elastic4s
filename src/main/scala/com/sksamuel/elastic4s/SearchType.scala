package com.sksamuel.elastic4s

/** @author Stephen Samuel */
abstract class SearchType(val elasticType: org.elasticsearch.action.search.SearchType)
case object SearchType {
  case object DfsQueryThenFetch extends SearchType(org.elasticsearch.action.search.SearchType.DFS_QUERY_THEN_FETCH)
  case object QueryThenFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_THEN_FETCH)
  case object DfsQueryAndFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_AND_FETCH)
  case object QueryAndFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_AND_FETCH)
  case object Scan extends SearchType(org.elasticsearch.action.search.SearchType.SCAN)
  case object Count extends SearchType(org.elasticsearch.action.search.SearchType.COUNT)
}
