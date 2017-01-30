package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.http.{HttpExecutable, Shards}
import com.sksamuel.elastic4s.searches.SearchDefinition
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.XContentFactory

import scala.collection.JavaConverters._

case class Hits(
                 total: Int,
                 max_score: Double,
                 hits: Array[Hit]
               ) {
  def size = hits.length
}

case class Hit()

case class SearchResponse(took: Int,
                          timed_out: Boolean,
                          _shards: Shards,
                          hits: Hits) {
  def totalHits: Int = hits.total
  def size = hits.size
}

trait SearchExecutables {

  implicit object SearchHttpExecutable extends HttpExecutable[SearchDefinition, SearchResponse] {

    override def execute(client: RestClient,
                         request: SearchDefinition): (ResponseListener) => Any = {

      val endpoint = "/" + request.indexesTypes.indexes.mkString(",") + "/" + request.indexesTypes.types.mkString(",") + "/_search"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.routing.foreach(params.put("routing", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.size.map(_.toString).foreach(params.put("size", _))
      request.searchType.map(_.toString).foreach(params.put("search_type", _))
      request.requestCache.map(_.toString).foreach(params.put("search_type", _))
      request.terminateAfter.map(_.toString).foreach(params.put("terminate_after", _))
      request.version.map(_.toString).foreach(params.put("version", _))

      val builder = XContentFactory.jsonBuilder()
      builder.startObject()
      request.query.map(QueryBuilderFn.apply).foreach(x => builder.rawField("query", new BytesArray(x.string)))
      builder.endObject()

      logger.debug("Executing search request: " + builder.string)

      val body = builder.string()
      val entity = new StringEntity(body)

      client.performRequestAsync("POST", endpoint, params.asJava, entity, _)
    }
  }
}
