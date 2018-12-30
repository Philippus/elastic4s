package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.exts.OptionImplicits._

case class GetMappingRequest(indexesAndTypes: IndexesAndTypes, local: Option[Boolean] = None) {

  def types(first: String, rest: String*): GetMappingRequest = types(first +: rest)

  def types(types: Seq[String]): GetMappingRequest =
    copy(indexesAndTypes = IndexesAndTypes(indexesAndTypes.indexes, types))

  def local(local: Boolean): GetMappingRequest = copy(local = local.some)
}
