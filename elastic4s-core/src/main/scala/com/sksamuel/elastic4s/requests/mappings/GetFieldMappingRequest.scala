package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.Indexes

case class GetFieldMappingRequest(indexes: Indexes, fields: Seq[String])
