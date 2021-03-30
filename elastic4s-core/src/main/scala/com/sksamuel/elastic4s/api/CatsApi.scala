package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.cat
import com.sksamuel.elastic4s.requests.cat.{CatAliases, CatAllocation, CatCount, CatHealth, CatIndexes, CatMaster, CatNodes, CatPlugins, CatSegments, CatShards, CatThreadPool}
import com.sksamuel.elastic4s.requests.common.HealthStatus
import com.sksamuel.exts.OptionImplicits._

trait CatsApi {

  def catAliases(): CatAliases                      = CatAliases(None)
  def catAliases(pattern: String): CatAliases       = CatAliases(pattern.some)

  def catAllocation(): CatAllocation = CatAllocation()

  def catCount(): CatCount                             = CatCount()
  def catCount(first: String, rest: String*): CatCount = CatCount(first +: rest)

  def catHealth(): CatHealth = CatHealth()

  def catIndices(): CatIndexes                     = CatIndexes(None, None)
  def catIndices(health: HealthStatus): CatIndexes = CatIndexes(health.some, None)
  def catIndices(indexPattern: String): CatIndexes = CatIndexes(None, indexPattern.some)

  def catMaster(): CatMaster = CatMaster()

  def catNodes(): CatNodes = CatNodes()

  def catPlugins(): CatPlugins = CatPlugins()

  def catSegments(indices: Indexes = Indexes.All): CatSegments = cat.CatSegments(indices)
  def catShards(): CatShards                                   = CatShards()

  def catThreadPool(): CatThreadPool = CatThreadPool()
}
