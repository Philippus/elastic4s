package com.sksamuel.elastic4s.alias

case class GetAliasDefinition(aliases: Seq[String],
                              indices: Seq[String] = Nil) {
  def on(indices: String*): GetAliasDefinition = copy(indices = indices)
}

