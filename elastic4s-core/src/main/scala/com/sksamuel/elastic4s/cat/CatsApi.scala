package com.sksamuel.elastic4s.cat

import com.sksamuel.exts.OptionImplicits._

trait CatsApi {

  def catAliases(): CatAliasesDefinition = CatAliasesDefinition()
  def catAllocation(): CatAllocationDefinition = CatAllocationDefinition()

  def catPlugins(): CatPluginsDefinition = CatPluginsDefinition()
  def catShards(): CatShardsDefinition = CatShardsDefinition()

  def catCount(): CatCountDefinition = CatCountDefinition()
  def catCount(first: String, rest: String*): CatCountDefinition = CatCountDefinition(first +: rest)
  def catNodes(): CatNodesDefinition = CatNodesDefinition()
  def catHealth(): CatHealthDefinition = CatHealthDefinition()
  def catThreadPool(): CatThreadPoolDefinition = CatThreadPoolDefinition()

  def catIndices(): CatIndexesDefinition = CatIndexesDefinition(None)
  def catIndices(health: Health): CatIndexesDefinition = CatIndexesDefinition(health.some)

  def catMaster(): CatMasterDefinition = CatMasterDefinition()
}

sealed trait Health
object Health {
  case object Green extends Health
  case object Yellow extends Health
  case object Red extends Health
}

case class CatPluginsDefinition()
case class CatShardsDefinition()
case class CatCountDefinition(indices: Seq[String] = Nil)
case class CatNodesDefinition()
case class CatHealthDefinition()
case class CatThreadPoolDefinition()
case class CatAllocationDefinition()
case class CatAliasesDefinition()
case class CatMasterDefinition()
case class CatIndexesDefinition(health: Option[Health])
