package com.sksamuel.elastic4s.http.search

import java.nio.charset.Charset

import cats.Show
import com.sksamuel.elastic4s.http.{HttpExecutable, IndicesOptionsParams, ResponseHandler}
import com.sksamuel.elastic4s.json.JacksonSupport
import com.sksamuel.elastic4s.searches.queries.term.{BuildableTermsQuery, TermsQueryDefinition}
import com.sksamuel.elastic4s.searches.{MultiSearchDefinition, SearchDefinition}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, RestClient}

import scala.concurrent.Future
import scala.io.{Codec, Source}
import scala.util.Try

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

    import scala.collection.JavaConverters._

    override def responseHandler: ResponseHandler[MultiSearchResponse] = new ResponseHandler[MultiSearchResponse] {
      override def onResponse(response: Response): Try[MultiSearchResponse] = Try {
        val charset = Option(response.getEntity.getContentEncoding).map(_.getValue).getOrElse("UTF-8")
        implicit val codec = Codec(Charset.forName(charset))
        val body = Source.fromInputStream(response.getEntity.getContent).mkString
        val json = JacksonSupport.mapper.readTree(body)
        val items = json.get("responses").elements.asScala.zipWithIndex.map { case (element, index) =>
          val status = element.get("status").intValue()
          val either = if (element.has("error"))
            Left(JacksonSupport.mapper.treeToValue[SearchError](element))
          else
            Right(JacksonSupport.mapper.treeToValue[SearchResponse](element))
          MultisearchResponseItem(index, status, either)
        }.toSeq
        MultiSearchResponse(items)
      }
    }

    override def execute(client: RestClient, request: MultiSearchDefinition): Future[Response] = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.maxConcurrentSearches.map(_.toString).foreach(params.put("max_concurrent_searches", _))

      val body = MultiSearchContentBuilder(request)
      logger.debug("Executing msearch: " + body)
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)
      client.async("POST", "/_msearch", params.toMap, entity)
    }
  }

  implicit object SearchHttpExecutable extends HttpExecutable[SearchDefinition, SearchResponse] {

    override def execute(client: RestClient, request: SearchDefinition): Future[Response] = {

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
      val body = builder.string()

      client.async("POST", endpoint, params.toMap, new StringEntity(body, ContentType.APPLICATION_JSON))
    }
  }
}
