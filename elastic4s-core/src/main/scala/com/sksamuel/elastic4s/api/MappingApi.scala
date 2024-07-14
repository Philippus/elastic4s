package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.mappings.{GetFieldMappingRequest, GetMappingRequest, PutMappingRequest}
import com.sksamuel.elastic4s.Indexes

trait MappingApi {

  val NotAnalyzed: String = "not_analyzed"

  def getMapping(str: String): GetMappingRequest = getMapping(Indexes(str))

  def getMapping(indexes: Indexes): GetMappingRequest = GetMappingRequest(indexes)

  def getMapping(indexes: Indexes, fields: String*): GetFieldMappingRequest = GetFieldMappingRequest(indexes, fields)

  def putMapping(indexes: Indexes): PutMappingRequest = PutMappingRequest(indexes)
}
