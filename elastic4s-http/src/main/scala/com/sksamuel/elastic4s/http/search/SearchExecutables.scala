package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.searches.SearchDefinition
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.XContentFactory

import scala.collection.JavaConverters._

trait SearchExecutables {

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
      request.size.map(_.toString).foreach(params.put("size", _))
      request.searchType.map(_.toString).foreach(params.put("search_type", _))
      request.requestCache.map(_.toString).foreach(params.put("search_type", _))
      request.terminateAfter.map(_.toString).foreach(params.put("terminate_after", _))
      request.version.map(_.toString).foreach(params.put("version", _))

      val builder = XContentFactory.jsonBuilder()
      builder.startObject()

      request.query.map(QueryBuilderFn.apply).foreach(x => builder.rawField("query", new BytesArray(x.string)))

      if (request.sorts.nonEmpty) {
        builder.startArray("sort")
        request.sorts.foreach { sort =>
          builder.rawValue(new BytesArray(SortContentBuilder(sort).string))
        }
        builder.endArray()
      }

      builder.endObject()


      logger.debug("Executing search request: " + builder.string)

      val body = builder.string()
      val entity = new StringEntity(body)

      client.performRequestAsync("POST", endpoint, params.asJava, entity, _)
    }
  }
}
