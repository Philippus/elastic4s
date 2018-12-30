package com.sksamuel.elastic4s.http.search

import java.net.URLEncoder

import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.json.JacksonSupport
import com.sksamuel.elastic4s.searches.queries.term.{BuildableTermsQuery, TermsQuery}
import com.sksamuel.elastic4s.searches.{MultiSearchRequest, SearchRequest, SearchType}
import org.apache.http.entity.ContentType

trait SearchHandlers {

  implicit def BuildableTermsNoOp[T]: BuildableTermsQuery[T] = new BuildableTermsQuery[T] {
    override def build(q: TermsQuery[T]): Any = null // not used by the http builders
  }

  implicit object MultiSearchHandler extends Handler[MultiSearchRequest, MultiSearchResponse] {

    import scala.collection.JavaConverters._

    override def responseHandler: ResponseHandler[MultiSearchResponse] = new ResponseHandler[MultiSearchResponse] {
      override def handle(response: HttpResponse) = {
        val json = JacksonSupport.mapper.readTree(response.entity.get.content)
        val items = json
          .get("responses")
          .elements
          .asScala
          .zipWithIndex
          .map {
            case (element, index) =>
              val status = element.get("status").intValue()
              val either =
                if (element.has("error"))
                  Left(JacksonSupport.mapper.treeToValue[SearchError](element.get("error")))
                else
                  Right(JacksonSupport.mapper.treeToValue[SearchResponse](element))
              MultisearchResponseItem(index, status, either)
          }
          .toSeq
        Right(MultiSearchResponse(items))
      }
    }

    override def build(request: MultiSearchRequest): ElasticRequest = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.maxConcurrentSearches.map(_.toString).foreach(params.put("max_concurrent_searches", _))
      request.typedKeys.map(_.toString).foreach(params.put("typed_keys", _))

      val body = MultiSearchBuilderFn(request)
      logger.debug("Executing msearch: " + body)
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)
      ElasticRequest("POST", "/_msearch", params.toMap, entity)
    }
  }

  implicit object SearchHandler extends Handler[SearchRequest, SearchResponse] {

    override def build(request: SearchRequest): ElasticRequest = {

      val endpoint =
        if (request.indexesTypes.indexes.isEmpty && request.indexesTypes.types.isEmpty)
          "/_search"
        else if (request.indexesTypes.indexes.isEmpty)
          "/_all/" + request.indexesTypes.types.map(URLEncoder.encode(_, "UTF-8")).mkString(",") + "/_search"
        else if (request.indexesTypes.types.isEmpty)
          "/" + request.indexesTypes.indexes.map(URLEncoder.encode(_, "UTF-8")).mkString(",") + "/_search"
        else
          "/" + request.indexesTypes.indexes
            .map(URLEncoder.encode(_, "UTF-8"))
            .mkString(",") + "/" + request.indexesTypes.types
            .map(URLEncoder.encode(_, "UTF-8"))
            .mkString(",") + "/_search"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.requestCache.map(_.toString).foreach(params.put("request_cache", _))
      request.searchType
        .filter(_ != SearchType.DEFAULT)
        .map(SearchTypeHttpParameters.convert)
        .foreach(params.put("search_type", _))
      request.control.routing.map(_.toString).foreach(params.put("routing", _))
      request.control.pref.foreach(params.put("preference", _))
      request.keepAlive.foreach(params.put("scroll", _))

      request.indicesOptions.foreach { opts =>
        IndicesOptionsParams(opts).foreach { case (key, value) => params.put(key, value) }
      }

      request.typedKeys.map(_.toString).foreach(params.put("typed_keys", _))

      val body = request.source.getOrElse(SearchBodyBuilderFn(request).string())
      ElasticRequest("POST", endpoint, params.toMap, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
    }
  }
}
