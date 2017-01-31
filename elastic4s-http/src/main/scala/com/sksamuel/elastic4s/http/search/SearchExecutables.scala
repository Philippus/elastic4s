package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.searches.{MultiSearchDefinition, SearchDefinition}
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}
import org.elasticsearch.common.xcontent.XContentFactory

import scala.collection.JavaConverters._

trait SearchExecutables {

  implicit object MultiSearchHttpExecutable extends HttpExecutable[MultiSearchDefinition, MultiSearchResponse] {

    override def execute(client: RestClient, request: MultiSearchDefinition): (ResponseListener) => Any = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.maxConcurrentSearches.map(_.toString).foreach(params.put("max_concurrent_searches", _))

      val body = request.searches.flatMap { search =>

        val header = XContentFactory.jsonBuilder()
        header.startObject()
        header.field("index", search.indexesTypes.indexes.mkString(","))
        if (search.indexesTypes.types.nonEmpty)
          header.field("type", search.indexesTypes.types.mkString(","))
        search.routing.foreach(header.field("routing", _))
        search.pref.foreach(header.field("preference", _))
        search.searchType.map(_.toString).foreach(header.field("search_type", _))
        header.endObject()

        val body = SearchContentBuilder(search)

        Seq(header.string(), body.string())
      }.mkString("\n") + "\n"

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
