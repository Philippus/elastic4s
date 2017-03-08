package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.{Indexes, IndexesAndTypes}

trait IndexAdminApi {

  def refreshIndex(first: String, rest: String*): RefreshIndexDefinition = refreshIndex(first +: rest)
  def refreshIndex(indexes: Iterable[String]): RefreshIndexDefinition = refreshIndex(Indexes(indexes))
  def refreshIndex(indexes: Indexes): RefreshIndexDefinition = RefreshIndexDefinition(indexes.values)

  def indexStats(indexes: Indexes): IndicesStatsDefinition = IndicesStatsDefinition(indexes)
  def indexStats(first: String, rest: String*): IndicesStatsDefinition = indexStats(first +: rest)

  def typesExist(indexesAndTypes: IndexesAndTypes) = TypesExistsDefinition(indexesAndTypes.indexes, indexesAndTypes.types)
  def typesExist(types: String*): TypesExistExpectsIn = typesExist(types)
  def typesExist(types: Iterable[String]): TypesExistExpectsIn = new TypesExistExpectsIn(types)
  class TypesExistExpectsIn(types: Iterable[String]) {
    def in(indexes: String*): TypesExistsDefinition = TypesExistsDefinition(indexes, types.toSeq)
  }

  def closeIndex(first: String, rest: String*): CloseIndexDefinition = CloseIndexDefinition(first +: rest)
  def openIndex(first: String, rest: String*): OpenIndexDefinition = OpenIndexDefinition(first +: rest)

  def getSegments(indexes: Indexes): GetSegmentsDefinition = GetSegmentsDefinition(indexes)
  def getSegments(first: String, rest: String*): GetSegmentsDefinition = getSegments(first +: rest)

  def flushIndex(indexes: Iterable[String]): FlushIndexDefinition = FlushIndexDefinition(indexes.toSeq)
  def flushIndex(indexes: String*): FlushIndexDefinition = flushIndex(indexes)

  def indexExists(index: String): IndexExistsDefinition = IndexExistsDefinition(index)

  def aliasExists(alias: String): AliasExistsDefinition = AliasExistsDefinition(alias)

  def clearCache(first: String, rest: String*): ClearCacheDefinition = clearCache(first +: rest)
  def clearCache(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)

  def clearIndex(first: String, rest: String*): ClearCacheDefinition = clearIndex(first +: rest)
  def clearIndex(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)

  def rollover(alias: String): RolloverDefinition = RolloverDefinition(alias)

  def shrink(source: String, target: String): ShrinkDefinition = ShrinkDefinition(source, target)

  def updateIndexLevelSettings(first: String, rest: String*): UpdateIndexLevelSettingsDefinition = updateIndexLevelSettings(first +: rest)
  def updateIndexLevelSettings(indexes: Iterable[String]): UpdateIndexLevelSettingsDefinition = updateIndexLevelSettings(Indexes(indexes))
  def updateIndexLevelSettings(indexes: Indexes): UpdateIndexLevelSettingsDefinition = UpdateIndexLevelSettingsDefinition(indexes.values)
}
