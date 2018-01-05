package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.searches.SearchType

object SearchTypeHttpParameters {
  def convert(searchType: SearchType): String = {
    searchType match {
      case SearchType.QUERY_THEN_FETCH => "query_then_fetch"
      case SearchType.DFS_QUERY_THEN_FETCH => "dfs_query_then_fetch"
    }
  }
}
