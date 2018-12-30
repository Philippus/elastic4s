package com.sksamuel.elastic4s.requests.alias

case class IndicesAliasesRequest(actions: Seq[AliasAction])

trait AliasAction
