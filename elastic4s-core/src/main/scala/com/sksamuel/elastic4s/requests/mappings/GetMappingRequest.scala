package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.Indexes

case class GetMappingRequest(indexes: Indexes, fields: Seq[String] = Nil) {
  def fields(first: String, rest: String*): GetMappingRequest = fields(first +: rest)

  def fields(_fields: Seq[String]): GetMappingRequest = copy(fields = _fields)
}
