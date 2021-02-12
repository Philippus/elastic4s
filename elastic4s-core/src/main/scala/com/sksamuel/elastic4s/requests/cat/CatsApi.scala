package com.sksamuel.elastic4s.requests.cat

import com.sksamuel.elastic4s.Indexes
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

  def catSegments(indices: Indexes = Indexes.All): CatSegments = CatSegments(indices)
  def catShards(): CatShards                                   = CatShards()

  def catThreadPool(): CatThreadPool = CatThreadPool()
}

case class CatSegments(indices: Indexes)
case class CatPlugins()
case class CatShards()
case class CatCount(indices: Seq[String] = Nil)
case class CatNodes()
case class CatHealth()
case class CatThreadPool()
case class CatAllocation()
case class CatAliases(pattern: Option[String])
case class CatMaster()
case class CatIndexes(health: Option[HealthStatus], indexPattern: Option[String])
