package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.common.IndicesOptionsParams
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}

import java.net.URLEncoder

trait AsyncSearchHandlers {

  implicit object AsyncSearchHandler extends Handler[AsyncSearchRequest, AsyncSearchResponse] {
    override def build(asyncRequest: AsyncSearchRequest): ElasticRequest = {
      val request = asyncRequest.innerRequest

      val endpoint =
        if (request.indexes.values.isEmpty)
          "/_all/_async_search"
        else
          "/" + request.indexes.values
            .map(URLEncoder.encode(_, "UTF-8"))
            .mkString(",") + "/_async_search"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.requestCache.map(_.toString).foreach(params.put("request_cache", _))
      request.searchType
        .filter(_ != SearchType.DEFAULT)
        .map(SearchTypeHttpParameters.convert)
        .foreach(params.put("search_type", _))
      request.routing.foreach(params.put("routing", _))
      request.pref.foreach(params.put("preference", _))
      request.allowPartialSearchResults.map(_.toString).foreach(params.put("allow_partial_search_results", _))
      request.batchedReduceSize.map(_.toString).foreach(params.put("batched_reduce_size", _))

      request.indicesOptions.foreach { opts =>
        IndicesOptionsParams(opts).foreach { case (key, value) => params.put(key, value) }
      }

      request.typedKeys.map(_.toString).foreach(params.put("typed_keys", _))

      asyncRequest.keepOnCompletion.map(_.toString).foreach(params.put("keep_on_completion", _))
      asyncRequest.waitForCompletionTimeout.map(_.toString).foreach(params.put("wait_for_completion_timeout", _))
      asyncRequest.keepAlive.foreach(params.put("keep_live", _))

      val body = request.source.getOrElse(SearchBodyBuilderFn(request).string())
      ElasticRequest("POST", endpoint, params.toMap, HttpEntity(body, "application/json"))
    }
  }

  implicit object FetchAsyncSearchHandler extends Handler[FetchAsyncSearchRequest, AsyncSearchResponse] {
    override def build(request: FetchAsyncSearchRequest): ElasticRequest = {
      logger.debug("Fetching async search with id: " + request.id)

      ElasticRequest("GET", s"/_async_search/${request.id}")
    }
  }

  implicit object DeleteAsyncSearchHandler extends Handler[DeleteAsyncSearchRequest, DeleteAsyncSearchResponse] {
    override def build(request: DeleteAsyncSearchRequest): ElasticRequest = {
      logger.debug("Deleting async search with id: " + request.id)

      ElasticRequest("DELETE", s"/_async_search/${request.id}")
    }
  }

  implicit object AsyncSearchStatusHandler extends Handler[AsyncSearchStatusRequest, AsyncSearchStatusResponse] {
    override def build(request: AsyncSearchStatusRequest): ElasticRequest = {
      logger.debug("Status of async search with id: " + request.id)

      ElasticRequest("GET", s"/_async_search/status/${request.id}")
    }
  }
}
