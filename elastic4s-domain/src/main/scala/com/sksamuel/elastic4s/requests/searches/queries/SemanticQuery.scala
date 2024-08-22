package com.sksamuel.elastic4s.requests.searches.queries

case class SemanticQuery(field: String, query: String) extends Query
