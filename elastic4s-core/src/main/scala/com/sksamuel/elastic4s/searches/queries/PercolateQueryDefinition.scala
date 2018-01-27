package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.DocumentRef

case class PercolateQueryDefinition(field: String,
                                    `type`: String,
                                    ref: Option[DocumentRef] = None,
                                    source: Option[String] = None)
    extends QueryDefinition
