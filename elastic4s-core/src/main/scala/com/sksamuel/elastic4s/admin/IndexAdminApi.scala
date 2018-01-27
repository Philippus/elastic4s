package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.{Indexes, IndexesAndTypes}

trait IndexAdminApi {

  def refreshIndex(first: String, rest: String*): RefreshIndex = refreshIndex(first +: rest)
  def refreshIndex(indexes: Iterable[String]): RefreshIndex    = refreshIndex(Indexes(indexes))
  def refreshIndex(indexes: Indexes): RefreshIndex             = RefreshIndex(indexes.values)

  def indexStats(indexes: Indexes = Indexes.All): IndexStats = IndexStats(indexes)
  def indexStats(first: String, rest: String*): IndexStats   = indexStats(first +: rest)

  def typesExist(indexesAndTypes: IndexesAndTypes)             = TypesExists(indexesAndTypes.indexes, indexesAndTypes.types)
  def typesExist(types: String*): TypesExistExpectsIn          = typesExist(types)
  def typesExist(types: Iterable[String]): TypesExistExpectsIn = new TypesExistExpectsIn(types)
  class TypesExistExpectsIn(types: Iterable[String]) {
    def in(indexes: String*): TypesExists = TypesExists(indexes, types.toSeq)
  }

  def closeIndex(first: String, rest: String*): CloseIndex = CloseIndex(first +: rest)
  def openIndex(first: String, rest: String*): OpenIndex   = OpenIndex(first +: rest)

  def getSegments(indexes: Indexes): GetSegments             = GetSegments(indexes)
  def getSegments(first: String, rest: String*): GetSegments = getSegments(first +: rest)

  def flushIndex(indexes: Iterable[String]): FlushIndex = FlushIndex(indexes.toSeq)
  def flushIndex(indexes: String*): FlushIndex          = flushIndex(indexes)

  def indexExists(index: String): IndicesExists      = IndicesExists(index)
  def indicesExists(indices: Indexes): IndicesExists = IndicesExists(indices)

  def aliasExists(alias: String): AliasExistsDefinition = AliasExistsDefinition(alias)

  def clearCache(first: String, rest: String*): ClearCache = clearCache(first +: rest)
  def clearCache(indexes: Iterable[String]): ClearCache    = ClearCache(indexes.toSeq)

  def clearIndex(first: String, rest: String*): ClearCache = clearIndex(first +: rest)
  def clearIndex(indexes: Iterable[String]): ClearCache    = ClearCache(indexes.toSeq)

  def rolloverIndex(alias: String): RolloverIndex = RolloverIndex(alias)

  @deprecated("use shrinkIndex(source, target)", "6.1.2")
  def shrink(source: String, target: String): ShrinkIndex      = ShrinkIndex(source, target)
  def shrinkIndex(source: String, target: String): ShrinkIndex = ShrinkIndex(source, target)

  def updateIndexLevelSettings(first: String, rest: String*): UpdateIndexLevelSettingsDefinition =
    updateIndexLevelSettings(first +: rest)
  def updateIndexLevelSettings(indexes: Iterable[String]): UpdateIndexLevelSettingsDefinition =
    updateIndexLevelSettings(Indexes(indexes))
  def updateIndexLevelSettings(indexes: Indexes): UpdateIndexLevelSettingsDefinition =
    UpdateIndexLevelSettingsDefinition(indexes.values)

  def indexShardStores(first: String, rest: String*): IndexShardStoreDefinition = indexShardStores(first +: rest)
  def indexShardStores(indexes: Iterable[String]): IndexShardStoreDefinition    = indexShardStores(Indexes(indexes))
  def indexShardStores(indexes: Indexes): IndexShardStoreDefinition             = IndexShardStoreDefinition(indexes)
}
