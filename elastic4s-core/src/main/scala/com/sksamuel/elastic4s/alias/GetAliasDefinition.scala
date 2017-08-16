package com.sksamuel.elastic4s.alias
import com.sksamuel.exts.OptionImplicits._

case class GetAliasDefinition(aliases: Seq[String],
                              indices: Seq[String] = Nil,
                              ignoreUnavailable: Option[Boolean] = None) {
  def on(indices: String*): GetAliasDefinition = copy(indices = indices)
  def ignoreUnavailable(ignore: Boolean): GetAliasDefinition = copy(ignoreUnavailable = ignore.some)
}

