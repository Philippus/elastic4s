package com.sksamuel.elastic4s.http.search

import java.net.URLEncoder

import cats.Show
import com.sksamuel.elastic4s.http.{
  HttpEntity,
  HttpExecutable,
  HttpRequestClient,
  HttpResponse,
  IndicesOptionsParams,
  ResponseHandler
}
import com.sksamuel.elastic4s.json.JacksonSupport
import com.sksamuel.elastic4s.searches.queries.term.{BuildableTermsQuery, TermsQueryDefinition}
import com.sksamuel.elastic4s.searches.{MultiSearchDefinition, SearchDefinition, SearchType}
import org.apache.http.entity.ContentType

import scala.concurrent.Future

trait SearchImplicits {

  implicit def BuildableTermsNoOp[T]: BuildableTermsQuery[T] = new BuildableTermsQuery[T] {
    override def build(q: TermsQueryDefinition[T]): Any = null // not used by the http builders
  }

  implicit object SearchShow extends Show[SearchDefinition] {
    override def show(req: SearchDefinition): String = SearchBodyBuilderFn(req).string()
  }

  implicit object MultiSearchShow extends Show[MultiSearchDefinition] {
    override def show(req: MultiSearchDefinition): String = MultiSearchBuilderFn(req)
  }

  implicit object MultiSearchHttpExecutable extends HttpExecutable[MultiSearchDefinition, MultiSearchResponse] {

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
                  Left(JacksonSupport.mapper.treeToValue[SearchError](element))
                else
                  Right(JacksonSupport.mapper.treeToValue[SearchResponse](element))
              MultisearchResponseItem(index, status, either)
          }
          .toSeq
        Right(MultiSearchResponse(items))
      }
    }

    override def execute(client: HttpRequestClient, request: MultiSearchDefinition): Future[HttpResponse] = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.maxConcurrentSearches.map(_.toString).foreach(params.put("max_concurrent_searches", _))

      val body = MultiSearchBuilderFn(request)
      logger.debug("Executing msearch: " + body)
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)
      client.async("POST", "/_msearch", params.toMap, entity)
    }
  }

  implicit object SearchHttpExecutable extends HttpExecutable[SearchDefinition, SearchResponse] {

    override def execute(client: HttpRequestClient, request: SearchDefinition): Future[HttpResponse] = {

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


      val body = request.source.getOrElse(SearchBodyBuilderFn(request).string())
      client.async("POST", endpoint, request.parameters, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
    }
  }

  implicit class RichSearchDefinition(request: SearchDefinition) {
    def parameters:Map[String,Any] = {
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
      params.toMap
    }
  }
}
