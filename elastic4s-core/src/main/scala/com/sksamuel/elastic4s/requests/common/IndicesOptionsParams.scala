package com.sksamuel.elastic4s.requests.common

import com.sksamuel.elastic4s.requests.admin.IndicesOptionsRequest

object IndicesOptionsParams {
  def apply(opts: IndicesOptionsRequest): Map[String, String] = {

    val expand =
      if (opts.expandWildcardClosed && opts.expandWildcardsOpen) "all"
      else if (opts.expandWildcardsOpen) "open"
      else if (opts.expandWildcardClosed) "closed"
      else "none"

    Map(
      "ignore_unavailable" -> opts.ignoreUnavailable.toString,
      "allow_no_indices"   -> opts.allowNoIndices.toString,
      "expand_wildcards"   -> expand
    )
  }
}
