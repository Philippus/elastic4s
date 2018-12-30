package com.sksamuel.elastic4s.requests.alias

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.exts.OptionImplicits._

case class GetAliasesRequest(indices: Indexes, aliases: Seq[String] = Nil, ignoreUnavailable: Option[Boolean] = None) {
  def ignoreUnavailable(ignore: Boolean): GetAliasesRequest = copy(ignoreUnavailable = ignore.some)
}
