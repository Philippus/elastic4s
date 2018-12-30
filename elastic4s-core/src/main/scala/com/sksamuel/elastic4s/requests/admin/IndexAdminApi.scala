package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.{Indexes, IndexesAndTypes}

trait IndexAdminApi {

  def refreshIndex(first: String, rest: String*): RefreshIndexRequest = refreshIndex(first +: rest)
  def refreshIndex(indexes: Iterable[String]): RefreshIndexRequest    = refreshIndex(Indexes(indexes))
  def refreshIndex(indexes: Indexes): RefreshIndexRequest             = RefreshIndexRequest(indexes.values)

  def indexStats(indexes: Indexes = Indexes.All): IndexStatsRequest = IndexStatsRequest(indexes)
  def indexStats(first: String, rest: String*): IndexStatsRequest   = indexStats(first +: rest)

  def typesExist(indexesAndTypes: IndexesAndTypes)             = TypesExistsRequest(indexesAndTypes.indexes, indexesAndTypes.types)
  def typesExist(types: String*): TypesExistExpectsIn          = typesExist(types)
  def typesExist(types: Iterable[String]): TypesExistExpectsIn = new TypesExistExpectsIn(types)
  class TypesExistExpectsIn(types: Iterable[String]) {
    def in(indexes: String*): TypesExistsRequest = TypesExistsRequest(indexes, types.toSeq)
  }

  def closeIndex(first: String, rest: String*): CloseIndexRequest = CloseIndexRequest(first +: rest)
  def openIndex(first: String, rest: String*): OpenIndexRequest   = OpenIndexRequest(first +: rest)

  def getSegments(indexes: Indexes): GetSegmentsRequest             = GetSegmentsRequest(indexes)
  def getSegments(first: String, rest: String*): GetSegmentsRequest = getSegments(first +: rest)

  def flushIndex(indexes: Iterable[String]): FlushIndexRequest = FlushIndexRequest(indexes.toSeq)
  def flushIndex(indexes: String*): FlushIndexRequest          = flushIndex(indexes)

  def indexExists(index: String): IndicesExistsRequest      = IndicesExistsRequest(index)
  def indicesExists(indices: Indexes): IndicesExistsRequest = IndicesExistsRequest(indices)

  def aliasExists(alias: String): AliasExistsRequest = AliasExistsRequest(alias)

  def clearCache(first: String, rest: String*): ClearCacheRequest = clearCache(first +: rest)
  def clearCache(indexes: Iterable[String]): ClearCacheRequest    = ClearCacheRequest(indexes.toSeq)

  def clearIndex(first: String, rest: String*): ClearCacheRequest = clearIndex(first +: rest)
  def clearIndex(indexes: Iterable[String]): ClearCacheRequest    = ClearCacheRequest(indexes.toSeq)

  def rolloverIndex(alias: String): RolloverIndexRequest = RolloverIndexRequest(alias)

  @deprecated("use shrinkIndex(source, target)", "6.1.2")
  def shrink(source: String, target: String): ShrinkIndexRequest      = ShrinkIndexRequest(source, target)
  def shrinkIndex(source: String, target: String): ShrinkIndexRequest = ShrinkIndexRequest(source, target)

  def updateIndexLevelSettings(first: String, rest: String*): UpdateIndexLevelSettingsRequest =
    updateIndexLevelSettings(first +: rest)
  def updateIndexLevelSettings(indexes: Iterable[String]): UpdateIndexLevelSettingsRequest =
    updateIndexLevelSettings(Indexes(indexes))
  def updateIndexLevelSettings(indexes: Indexes): UpdateIndexLevelSettingsRequest =
    UpdateIndexLevelSettingsRequest(indexes.values)

  def indexShardStores(first: String, rest: String*): IndexShardStoreRequest = indexShardStores(first +: rest)
  def indexShardStores(indexes: Iterable[String]): IndexShardStoreRequest    = indexShardStores(Indexes(indexes))
  def indexShardStores(indexes: Indexes): IndexShardStoreRequest             = IndexShardStoreRequest(indexes)
}
