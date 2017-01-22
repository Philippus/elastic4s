package com.sksamuel.elastic4s.searches.queries.span

import com.sksamuel.elastic4s.searches.queries.{MultiTermQueryDefinition, QueryDefinition}

trait SpanQueryDefinition extends QueryDefinition

case class SpanMultiTermQueryDefinition(query: MultiTermQueryDefinition) extends SpanQueryDefinition

case class SpanFirstQueryDefinition(query: SpanQueryDefinition, end: Int) extends QueryDefinition


