package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.{Index, IndexAndType}

trait GetApi {

  // prefered syntax as of 6.0
  def get(index: Index, `type`: String, id: String) = GetRequest(IndexAndType(index.name, `type`), id)

  def get(id: String): GetExpectsFrom = new GetExpectsFrom(id)
  class GetExpectsFrom(id: String) {

    def from(str: String): GetRequest =
      if (str.contains('/')) from(IndexAndType(str)) else from(IndexAndType(str, "_all"))

    def from(index: (String, String)): GetRequest       = from(IndexAndType(index._1, index._2))
    def from(index: String, `type`: String): GetRequest = from(IndexAndType(index, `type`))
    def from(index: IndexAndType): GetRequest           = GetRequest(index, id)
  }

  def multiget(first: GetRequest, rest: GetRequest*): MultiGetRequest = multiget(first +: rest)
  def multiget(gets: Iterable[GetRequest]): MultiGetRequest           = MultiGetRequest(gets.toSeq)
}
