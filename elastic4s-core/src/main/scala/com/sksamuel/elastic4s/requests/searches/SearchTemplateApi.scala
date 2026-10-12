package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.Indexes

case class TemplateSearchRequest(indexes: Indexes, name: String, params: Map[String, Any])
