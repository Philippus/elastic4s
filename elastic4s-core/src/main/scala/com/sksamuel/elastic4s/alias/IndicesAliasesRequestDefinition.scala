package com.sksamuel.elastic4s.alias

case class IndicesAliasesRequestDefinition(actions: Seq[AliasActionDefinition])

trait AliasActionDefinition
