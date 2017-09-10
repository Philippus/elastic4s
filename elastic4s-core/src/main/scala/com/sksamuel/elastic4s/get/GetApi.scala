package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.IndexAndType

import scala.language.implicitConversions

trait GetApi {

  def get(indexname: String, id: Any) = GetDefinition(indexname, id.toString)
  def get(id: Any): GetExpectsFrom = new GetExpectsFrom(id)
  class GetExpectsFrom(id: Any) {

    def from(str: String): GetDefinition = {
      if (str.contains('/')) from(IndexAndType(str)) else from(IndexAndType(str, "_all"))
    }

    @deprecated("Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. Therefore getting a document from a specified type will no longer work in the next release. Use get(id).from(index) or get(index, id)", "6.0")
    def from(index: (String, String)): GetDefinition = from(IndexAndType(index._1, index._2))

    @deprecated("Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. Therefore getting a document from a specified type will no longer work in the next release. Use get(id).from(index) or get(index, id)", "6.0")
    def from(index: String, `type`: String): GetDefinition = from(IndexAndType(index, `type`))

    @deprecated("Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. Therefore getting a document from a specified type will no longer work in the next release. Use get(id).from(index) or get(index, id)", "6.0")
    def from(index: IndexAndType): GetDefinition = GetDefinition(index, id.toString)
  }

  def multiget(first: GetDefinition, rest: GetDefinition*): MultiGetDefinition = multiget(first +: rest)
  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = MultiGetDefinition(gets.toSeq)
}
