package com.sksamuel.elastic4s.http.search

import cats.Show
import com.sksamuel.elastic4s.http.{HttpExecutable, IndicesOptionsParams, ResponseHandler}
import com.sksamuel.elastic4s.searches.queries.term.{BuildableTermsQuery, TermsQueryDefinition}
import com.sksamuel.elastic4s.searches.{MultiSearchDefinition, SearchDefinition}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

trait SearchImplicits {

  implicit def BuildableTermsNoOp[T] = new BuildableTermsQuery[T] {
    override def build(q: TermsQueryDefinition[T]): Any = null // not used by the http builders
  }

  implicit object SearchShow extends Show[SearchDefinition] {
    override def show(req: SearchDefinition): String = SearchBodyBuilderFn(req).string()
  }

  implicit object MultiSearchShow extends Show[MultiSearchDefinition] {
    override def show(req: MultiSearchDefinition): String = MultiSearchContentBuilder(req)
  }

  implicit object MultiSearchHttpExecutable extends HttpExecutable[MultiSearchDefinition, MultiSearchResponse] {

    override def execute(client: RestClient,
                         request: MultiSearchDefinition): Future[MultiSearchResponse] = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.maxConcurrentSearches.map(_.toString).foreach(params.put("max_concurrent_searches", _))

      val body = MultiSearchContentBuilder(request)
      logger.debug("Executing msearch: " + body)
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)
      client.async("POST", "/_msearch", params.toMap, entity, ResponseHandler.default)
    }
  }

  implicit object SearchHttpExecutable extends HttpExecutable[SearchDefinition, SearchResponse] {

    override def execute(client: RestClient,
                         request: SearchDefinition): Future[SearchResponse] = {

      val endpoint = if (request.indexesTypes.indexes.isEmpty && request.indexesTypes.types.isEmpty)
        "/_search"
      else if (request.indexesTypes.indexes.isEmpty)
        "/_all/" + request.indexesTypes.types.mkString(",") + "/_search"
      else if (request.indexesTypes.types.isEmpty)
        "/" + request.indexesTypes.indexes.mkString(",") + "/_search"
      else
        "/" + request.indexesTypes.indexes.mkString(",") + "/" + request.indexesTypes.types.mkString(",") + "/_search"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.keepAlive.foreach(params.put("scroll", _))
      request.pref.foreach(params.put("preference", _))
      request.requestCache.map(_.toString).foreach(params.put("request_cache", _))
      request.routing.foreach(params.put("routing", _))
      request.searchType.map(_.toString).foreach(params.put("search_type", _))
      request.terminateAfter.map(_.toString).foreach(params.put("terminate_after", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.indicesOptions.foreach { opts =>
        IndicesOptionsParams(opts).foreach { case (key, value) => params.put(key, value) }
      }

      val builder = SearchBodyBuilderFn(request)
      logger.debug("Executing search request: " + builder.string)

      val body = builder.string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)
      client.async("POST", endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }
}
