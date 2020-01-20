package com.sksamuel.elastic4s.requests.searches.queries

case class PinnedQuery(ids: List[String], organic: Query) extends Query
