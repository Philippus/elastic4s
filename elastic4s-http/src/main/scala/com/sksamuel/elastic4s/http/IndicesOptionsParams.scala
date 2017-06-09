package com.sksamuel.elastic4s.http

import org.elasticsearch.action.support.IndicesOptions

object IndicesOptionsParams {
  def apply(opts: IndicesOptions): Map[String, String] = {

    val expand = if (opts.expandWildcardsClosed() && opts.expandWildcardsOpen) "all"
    else if (opts.expandWildcardsOpen) "open"
    else if (opts.expandWildcardsClosed) "closed"
    else "none"

    Map(
      "ignore_unavailable" -> opts.ignoreUnavailable.toString,
      "allow_no_indices" -> opts.allowNoIndices.toString,
      "expand_wildcards" -> expand
    )
  }
}
