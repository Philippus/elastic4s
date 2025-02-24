package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.json.XContentBuilder
import com.sksamuel.elastic4s.requests.common.IndicesOptionsParams
import com.sksamuel.elastic4s.requests.searches.aggs.AbstractAggregation
import com.sksamuel.elastic4s.{
  ElasticError,
  ElasticRequest,
  ElasticUrlEncoder,
  Handler,
  HttpEntity,
  HttpResponse,
  JacksonSupport,
  ResponseHandler
}

trait SearchHandlers {

  class BaseMultiSearchHandler(customAggregationHandler: PartialFunction[AbstractAggregation, XContentBuilder])
      extends Handler[MultiSearchRequest, MultiSearchResponse] {

    import scala.collection.JavaConverters._

    override def responseHandler: ResponseHandler[MultiSearchResponse] = new ResponseHandler[MultiSearchResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, MultiSearchResponse] =
        response.statusCode match {
          case status if status >= 200 && status < 300 =>
            val json  = JacksonSupport.mapper.readTree(response.entity.get.content)
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
              case None       => Nil
            }
            Right(MultiSearchResponse(items))
          case _                                       =>
            Left(ElasticErrorParser.parse(response))
        }
    }

    override def build(request: MultiSearchRequest): ElasticRequest = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.maxConcurrentSearches.map(_.toString).foreach(params.put("max_concurrent_searches", _))
      request.typedKeys.map(_.toString).foreach(params.put("typed_keys", _))

      val body   = MultiSearchBuilderFn(request, customAggregationHandler)
      logger.debug("Executing msearch: " + body)
      val entity = HttpEntity(body, "application/x-ndjson")
      ElasticRequest("POST", "/_msearch", params.toMap, entity)
    }
  }

  implicit object MultiSearchHandler extends BaseMultiSearchHandler(defaultCustomAggregationHandler)

  class BaseSearchHandler(customAggregationHandler: PartialFunction[AbstractAggregation, XContentBuilder])
      extends Handler[SearchRequest, SearchResponse] {

    override def build(request: SearchRequest): ElasticRequest = {

      val endpoint = {
        if (request.indexes.values.isEmpty && request.pit.isDefined)
          "/_search"
        else if (request.indexes.values.isEmpty)
          "/_all/_search"
        else
          "/" + request.indexes.values
            .map(ElasticUrlEncoder.encodeUrlFragment)
            .mkString(",") + "/_search"
      }

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

      val body = request.source.getOrElse(SearchBodyBuilderFn(request, customAggregationHandler).string)
      ElasticRequest("POST", endpoint, params.toMap, HttpEntity(body, "application/json"))
    }
  }
  implicit object SearchHandler extends BaseSearchHandler(defaultCustomAggregationHandler)
}

object SearchHandlers extends SearchHandlers
