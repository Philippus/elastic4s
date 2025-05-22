package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.requests.get.{GetRequest, MultiGetRequest}

trait GetApi {
  def get(index: Index, id: String): GetRequest                       = GetRequest(index, id)
  def multiget(first: GetRequest, rest: GetRequest*): MultiGetRequest = multiget(first +: rest)
  def multiget(gets: Iterable[GetRequest]): MultiGetRequest           = MultiGetRequest(gets.toSeq)
}
