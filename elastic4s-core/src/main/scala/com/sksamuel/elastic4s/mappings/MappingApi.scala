package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.{Indexes, IndexesAndType, IndexesAndTypes}

import scala.language.implicitConversions

trait MappingApi {

  val NotAnalyzed: String = "not_analyzed"

  def getMapping(str: String): GetMappingDefinition =
    if (str.contains("/")) getMapping(IndexesAndTypes(str)) else getMapping(Indexes(str))

  def getMapping(indexes: Indexes): GetMappingDefinition = getMapping(indexes.toIndexesAndTypes)
  def getMapping(indexesAndTypes: IndexesAndTypes): GetMappingDefinition = GetMappingDefinition(indexesAndTypes)

  def putMapping(indexesAndType: IndexesAndType): PutMappingDefinition = PutMappingDefinition(indexesAndType)
}






