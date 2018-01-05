package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.searches.SearchType

object EnumConversions {
  def searchType(searchType: SearchType): org.elasticsearch.action.search.SearchType = {
    searchType match {
      case SearchType.DfsQueryThenFetch => org.elasticsearch.action.search.SearchType.DFS_QUERY_THEN_FETCH
      case SearchType.QueryThenFetch => org.elasticsearch.action.search.SearchType.QUERY_THEN_FETCH
    }
  }
}
