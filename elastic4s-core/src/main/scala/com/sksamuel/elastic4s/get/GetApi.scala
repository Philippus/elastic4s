package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.{Index, IndexAndType}

import scala.language.implicitConversions

trait GetApi {

  // prefered syntax as of 6.0
  def get(index: Index, `type`: String, id: String) = GetDefinition(IndexAndType(index.name, `type`), id)

  def get(id: String): GetExpectsFrom = new GetExpectsFrom(id)
  class GetExpectsFrom(id: String) {

    def from(str: String): GetDefinition = {
      if (str.contains('/')) from(IndexAndType(str)) else from(IndexAndType(str, "_all"))
    }

    def from(index: (String, String)): GetDefinition = from(IndexAndType(index._1, index._2))
    def from(index: String, `type`: String): GetDefinition = from(IndexAndType(index, `type`))
    def from(index: IndexAndType): GetDefinition = GetDefinition(index, id)
  }

  def multiget(first: GetDefinition, rest: GetDefinition*): MultiGetDefinition = multiget(first +: rest)
  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = MultiGetDefinition(gets.toSeq)
}
