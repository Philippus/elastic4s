package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.exts.OptionImplicits._

case class GetAliasesDefinition(indices: Indexes,
                                aliases: Seq[String] = Nil,
                                ignoreUnavailable: Option[Boolean] = None) {
  def ignoreUnavailable(ignore: Boolean): GetAliasesDefinition = copy(ignoreUnavailable = ignore.some)
}
