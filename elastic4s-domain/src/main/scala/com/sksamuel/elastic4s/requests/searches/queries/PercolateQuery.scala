package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.common.DocumentRef

case class PercolateQuery(field: String, `type`: String, ref: Option[DocumentRef] = None, source: Option[String] = None)
    extends Query
