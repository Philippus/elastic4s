package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.common.IndicesOptionsParams
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, ElasticUrlEncoder, Handler, HttpEntity, HttpResponse, JacksonSupport, ResponseHandler}

trait SearchHandlers {

  implicit object MultiSearchHandler extends Handler[MultiSearchRequest, MultiSearchResponse] {

    import scala.collection.JavaConverters._

    override def responseHandler: ResponseHandler[MultiSearchResponse] = new ResponseHandler[MultiSearchResponse] {
      override def handle(response: HttpResponse): Right[Nothing, MultiSearchResponse] = {
        val json = JacksonSupport.mapper.readTree(response.entity.get.content)
        val items = Option(json.get("responses")) match {
          case Some(node) =>
            node.elements
              .asScala
              .zipWithIndex
              .map {
                case (element, index) =>
                  val status = element.get("status").intValue()
                  val either =
                    if (element.has("error"))
                      Left(JacksonSupport.mapper.treeToValue[ElasticError](element.get("error")))
                    else
                      Right(JacksonSupport.mapper.treeToValue[SearchResponse](element))
                  MultisearchResponseItem(index, status, either)
              }.toSeq
          case None => Nil
        }
        Right(MultiSearchResponse(items))
      }
    }

    override def build(request: MultiSearchRequest): ElasticRequest = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.maxConcurrentSearches.map(_.toString).foreach(params.put("max_concurrent_searches", _))
      request.typedKeys.map(_.toString).foreach(params.put("typed_keys", _))

      val body = MultiSearchBuilderFn(request)
      logger.debug("Executing msearch: " + body)
      val entity = HttpEntity(body, "application/json")
      ElasticRequest("POST", "/_msearch", params.toMap, entity)
    }
  }

  implicit object SearchHandler extends Handler[SearchRequest, SearchResponse] {

    override def build(request: SearchRequest): ElasticRequest = {

      val endpoint =
        if (request.indexes.values.isEmpty)
          "/_all/_search"
        else
          "/" + request.indexes.values
            .map(ElasticUrlEncoder.encodeUrlFragment)
            .mkString(",") + "/_search"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.requestCache.map(_.toString).foreach(params.put("request_cache", _))
      request.searchType
        .filter(_ != SearchType.DEFAULT)
        .map(SearchTypeHttpParameters.convert)
        .foreach(params.put("search_type", _))
      request.routing.map(_.toString).foreach(params.put("routing", _))
      request.pref.foreach(params.put("preference", _))
      request.keepAlive.foreach(params.put("scroll", _))
      request.allowPartialSearchResults.map(_.toString).foreach(params.put("allow_partial_search_results", _))
      request.batchedReduceSize.map(_.toString).foreach(params.put("batched_reduce_size", _))

      request.indicesOptions.foreach { opts =>
        IndicesOptionsParams(opts).foreach { case (key, value) => params.put(key, value) }
      }

      request.typedKeys.map(_.toString).foreach(params.put("typed_keys", _))

      val body = request.source.getOrElse(SearchBodyBuilderFn(request).string())
      ElasticRequest("POST", endpoint, params.toMap, HttpEntity(body, "application/json"))
    }
  }
}

object SearchHandlers extends SearchHandlers
