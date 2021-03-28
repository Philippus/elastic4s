package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.requests.searches.queries.Query

@deprecated("Use the stored scripts api", "6.0.0")
case class PutSearchTemplateRequest(name: String, query: Option[Query], body: Option[String])
case class GetSearchTemplateRequest(name: String)

@deprecated("Use the stored scripts api", "6.0.0")
case class RemoveSearchTemplateRequest(name: String)
case class TemplateSearchRequest(indexesAndTypes: IndexesAndTypes, name: String, params: Map[String, Any])
