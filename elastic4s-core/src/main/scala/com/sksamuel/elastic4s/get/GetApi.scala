package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.IndexAndType

import scala.language.implicitConversions

trait GetApi {

  def get(id: Any): GetExpectsFrom = new GetExpectsFrom(id)
  class GetExpectsFrom(id: Any) {
    def from(str: String): GetDefinition = {
      if (str.contains('/')) from(IndexAndType(str)) else from(IndexAndType(str, "_all"))
    }
    def from(index: (String, String)): GetDefinition = from(IndexAndType(index._1, index._2))
    def from(index: String, `type`: String): GetDefinition = from(IndexAndType(index, `type`))
    def from(index: IndexAndType): GetDefinition = GetDefinition(index, id.toString)
  }

  def multiget(first: GetDefinition, rest: GetDefinition*): MultiGetDefinition = multiget(first +: rest)
  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = MultiGetDefinition(gets.toSeq)
}
