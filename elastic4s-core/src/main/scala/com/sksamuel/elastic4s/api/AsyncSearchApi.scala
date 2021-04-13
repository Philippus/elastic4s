package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.{AsyncSearchRequest, DeleteAsyncSearchRequest, FetchAsyncSearchRequest}
import com.sksamuel.elastic4s.{Index, Indexes}

trait AsyncSearchApi {

  def asyncSearch(index: String): AsyncSearchRequest = asyncSearch(Indexes(index))
  def asyncSearch(first: String, rest: String*): AsyncSearchRequest = asyncSearch(first +: rest)
  def asyncSearch(index: Index): AsyncSearchRequest = asyncSearch(index.name)
  def asyncSearch(indexes: Iterable[String]): AsyncSearchRequest = asyncSearch(Indexes(indexes.toSeq))
  def asyncSearch(indexes: Indexes): AsyncSearchRequest = AsyncSearchRequest(indexes)

  def fetchAsyncSearch(id: String): FetchAsyncSearchRequest = FetchAsyncSearchRequest(id)

  def clearAsyncSearch(id: String): DeleteAsyncSearchRequest = DeleteAsyncSearchRequest(id)

}
