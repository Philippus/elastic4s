package com.sksamuel.elastic4s.cat

import com.sksamuel.exts.OptionImplicits._

trait CatsApi {
  def catAliases(): CatAliasesDefinition = CatAliasesDefinition()
  def catIndices(): CatIndexesDefinition = CatIndexesDefinition(None)
  def catIndices(health: Health): CatIndexesDefinition = CatIndexesDefinition(health.some)
  def catMaster(): CatMasterDefinition = CatMasterDefinition()
  def catAllocation(): CatAllocationDefinition = CatAllocationDefinition()
}

sealed trait Health
object Health {
  case object Green extends Health
  case object Yellow extends Health
  case object Red extends Health
}

case class CatAllocationDefinition()
case class CatAliasesDefinition()
case class CatMasterDefinition()
case class CatIndexesDefinition(health: Option[Health])
