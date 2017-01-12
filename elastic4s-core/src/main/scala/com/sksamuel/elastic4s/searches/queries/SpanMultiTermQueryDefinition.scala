package com.sksamuel.elastic4s.searches.queries

trait SpanQueryDefinition extends QueryDefinition

case class SpanMultiTermQueryDefinition(query: MultiTermQueryDefinition) extends SpanQueryDefinition

case class SpanFirstQueryDefinition(query: SpanQueryDefinition, end: Int) extends QueryDefinition


