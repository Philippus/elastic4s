package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.{Indexes, IndexesAndType}

import scala.language.implicitConversions

trait MappingApi {

  val NotAnalyzed: String = "not_analyzed"

  def getMapping(str: String): GetMappingRequest = getMapping(Indexes(str))

  def getMapping(indexes: Indexes): GetMappingRequest               = GetMappingRequest(indexes)

  def getMapping(indexes: Indexes, fields: String*): GetFieldMappingRequest = GetFieldMappingRequest(indexes, fields)

  def putMapping(indexes: Indexes): PutMappingRequest               = PutMappingRequest(IndexesAndType(indexes))

  @deprecated("types are deprecated now", "7.0")
  def putMapping(indexesAndType: IndexesAndType): PutMappingRequest = PutMappingRequest(indexesAndType)
}
