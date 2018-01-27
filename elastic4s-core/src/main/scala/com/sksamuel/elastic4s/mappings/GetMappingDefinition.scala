package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.exts.OptionImplicits._

case class GetMappingDefinition(indexesAndTypes: IndexesAndTypes, local: Option[Boolean] = None) {

  def types(first: String, rest: String*): GetMappingDefinition = types(first +: rest)

  def types(types: Seq[String]): GetMappingDefinition =
    copy(indexesAndTypes = IndexesAndTypes(indexesAndTypes.indexes, types))

  def local(local: Boolean): GetMappingDefinition = copy(local = local.some)
}
