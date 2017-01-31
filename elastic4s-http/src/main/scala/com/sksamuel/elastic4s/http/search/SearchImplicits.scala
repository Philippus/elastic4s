package com.sksamuel.elastic4s.http.search

import cats.Show
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.searches.{MultiSearchDefinition, SearchDefinition}
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._

trait SearchImplicits {

  implicit object SearchShow extends Show[SearchDefinition] {
    override def show(req: SearchDefinition): String = SearchContentBuilder(req).string()
  }

  implicit object MultiSearchShow extends Show[MultiSearchDefinition] {
    override def show(req: MultiSearchDefinition): String = MultiSearchContentBuilder(req)
  }

  implicit object MultiSearchHttpExecutable extends HttpExecutable[MultiSearchDefinition, MultiSearchResponse] {

    override def execute(client: RestClient, request: MultiSearchDefinition): (ResponseListener) => Any = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.maxConcurrentSearches.map(_.toString).foreach(params.put("max_concurrent_searches", _))

      val body = MultiSearchContentBuilder(request)
      logger.debug("Executing msearch: " + body)
      val entity = new StringEntity(body)

      client.performRequestAsync("POST", "/_msearch", params.asJava, entity, _)
    }
  }

  implicit object SearchHttpExecutable extends HttpExecutable[SearchDefinition, SearchResponse] {

    override def execute(client: RestClient,
                         request: SearchDefinition): (ResponseListener) => Any = {

      val endpoint = if (request.indexesTypes.indexes.isEmpty && request.indexesTypes.types.isEmpty)
        "/_search"
      else if (request.indexesTypes.indexes.isEmpty)
        "/_all/" + request.indexesTypes.types.mkString(",") + "/_search"
      else if (request.indexesTypes.types.isEmpty)
        "/" + request.indexesTypes.indexes.mkString(",") + "/_search"
      else
        "/" + request.indexesTypes.indexes.mkString(",") + "/" + request.indexesTypes.types.mkString(",") + "/_search"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.routing.foreach(params.put("routing", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.pref.foreach(params.put("preference", _))
      request.size.map(_.toString).foreach(params.put("size", _))
      request.searchType.map(_.toString).foreach(params.put("search_type", _))
      request.requestCache.map(_.toString).foreach(params.put("search_type", _))
      request.terminateAfter.map(_.toString).foreach(params.put("terminate_after", _))
      request.version.map(_.toString).foreach(params.put("version", _))

      val builder = SearchContentBuilder(request)
      logger.debug("Executing search request: " + builder.string)

      val body = builder.string()
      val entity = new StringEntity(body)

      client.performRequestAsync("POST", endpoint, params.asJava, entity, _)
    }
  }
}
