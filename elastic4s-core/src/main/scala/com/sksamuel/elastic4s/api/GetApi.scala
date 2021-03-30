package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.{Index, IndexAndType}
import com.sksamuel.elastic4s.requests.get.{GetRequest, MultiGetRequest}

trait GetApi {

  @deprecated("types are deprecated now", "7.0")
  def get(index: Index, `type`: String, id: String) = GetRequest(IndexAndType(index.name, `type`), id)

  def get(index: Index, id: String): GetRequest = GetRequest(index, id)

  @deprecated("use get(index, id)", "7.7")
  def get(id: String): GetExpectsFrom = new GetExpectsFrom(id)
  class GetExpectsFrom(id: String) {
    def from(index: Index): GetRequest           = GetRequest(index, id)
  }

  def multiget(first: GetRequest, rest: GetRequest*): MultiGetRequest = multiget(first +: rest)
  def multiget(gets: Iterable[GetRequest]): MultiGetRequest           = MultiGetRequest(gets.toSeq)
}
