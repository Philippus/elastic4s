package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.{AsyncSearchRequest, AsyncSearchStatusRequest, DeleteAsyncSearchRequest, FetchAsyncSearchRequest, SearchRequest}

trait AsyncSearchApi {

  def asyncSearch(searchRequest: SearchRequest): AsyncSearchRequest = AsyncSearchRequest(searchRequest)

  def asyncSearchStatus(id: String): AsyncSearchStatusRequest = AsyncSearchStatusRequest(id)

  def fetchAsyncSearch(id: String): FetchAsyncSearchRequest = FetchAsyncSearchRequest(id)

  def clearAsyncSearch(id: String): DeleteAsyncSearchRequest = DeleteAsyncSearchRequest(id)

}
