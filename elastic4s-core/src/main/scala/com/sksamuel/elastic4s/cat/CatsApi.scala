package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.{HealthStatus, Indexes}
import com.sksamuel.exts.OptionImplicits._

trait CatsApi {

  def catAliases(): CatAliasesDefinition = CatAliasesDefinition()
  def catAllocation(): CatAllocationDefinition = CatAllocationDefinition()

  def catCount(): CatCountDefinition = CatCountDefinition()
  def catCount(first: String, rest: String*): CatCountDefinition = CatCountDefinition(first +: rest)

  def catHealth(): CatHealthDefinition = CatHealthDefinition()

  def catIndices(): CatIndexesDefinition = CatIndexesDefinition(None)
  def catIndices(health: HealthStatus): CatIndexesDefinition = CatIndexesDefinition(health.some)

  def catMaster(): CatMasterDefinition = CatMasterDefinition()

  def catNodes(): CatNodesDefinition = CatNodesDefinition()

  def catPlugins(): CatPluginsDefinition = CatPluginsDefinition()

  def catSegments(indices: Indexes = Indexes.All): CatSegments = CatSegments(indices)
  def catShards(): CatShardsDefinition = CatShardsDefinition()

  def catThreadPool(): CatThreadPoolDefinition = CatThreadPoolDefinition()
}

case class CatSegments(indices: Indexes)

case class CatPluginsDefinition()
case class CatShardsDefinition()
case class CatCountDefinition(indices: Seq[String] = Nil)
case class CatNodesDefinition()
case class CatHealthDefinition()
case class CatThreadPoolDefinition()
case class CatAllocationDefinition()
case class CatAliasesDefinition()
case class CatMasterDefinition()
case class CatIndexesDefinition(health: Option[HealthStatus])
