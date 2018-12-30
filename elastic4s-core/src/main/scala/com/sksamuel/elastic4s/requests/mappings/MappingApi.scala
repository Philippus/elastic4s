package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.{Indexes, IndexesAndType, IndexesAndTypes}

import scala.language.implicitConversions

trait MappingApi {

  val NotAnalyzed: String = "not_analyzed"

  def getMapping(str: String): GetMappingRequest =
    if (str.contains("/")) getMapping(IndexesAndTypes(str)) else getMapping(Indexes(str))

  def getMapping(indexes: Indexes): GetMappingRequest                 = getMapping(indexes.toIndexesAndTypes)
  def getMapping(indexesAndTypes: IndexesAndTypes): GetMappingRequest = GetMappingRequest(indexesAndTypes)

  def putMapping(indexesAndType: IndexesAndType): PutMappingRequest = PutMappingRequest(indexesAndType)
}
