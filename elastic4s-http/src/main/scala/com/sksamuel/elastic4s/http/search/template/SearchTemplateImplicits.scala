package com.sksamuel.elastic4s.http.search.template

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.searches.{GetSearchTemplateDefinition, PutSearchTemplateDefinition, RemoveSearchTemplateDefinition, TemplateSearchDefinition}
import com.sksamuel.exts.OptionImplicits._
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, ResponseException, RestClient}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait SearchTemplateImplicits {

  implicit object TemplateSearchExecutable
    extends HttpExecutable[TemplateSearchDefinition, SearchResponse] {

    override def execute(client: RestClient, req: TemplateSearchDefinition): Future[SearchResponse] = {
      val method = "POST"
      val endpoint = req.indexesAndTypes match {
        case IndexesAndTypes(Nil, Nil) => "/_search/template"
        case IndexesAndTypes(indexes, Nil) => "/" + indexes.mkString(",") + "/_search/template"
        case IndexesAndTypes(Nil, types) => "/_all/" + types.mkString(",") + "/_search/template"
        case IndexesAndTypes(indexes, types) => "/" + indexes.mkString(",") + "/" + types.mkString(",") + "/_search/template"
      }
      val body = TemplateSearchContentBuilder(req).string()
      client.async(method, endpoint, Map.empty, new StringEntity(body, ContentType.APPLICATION_JSON), ResponseHandler.default)
    }
  }

  implicit object RemoveSearchTemplateExecutable
    extends HttpExecutable[RemoveSearchTemplateDefinition, RemoveSearchTemplateResponse] {

    override def execute(client: RestClient, req: RemoveSearchTemplateDefinition): Future[RemoveSearchTemplateResponse] = {
      val method = "DELETE"
      val endpoint = "/_search/template/" + req.name
      client.async(method, endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object PutSearchTemplateExecutable
    extends HttpExecutable[PutSearchTemplateDefinition, PutSearchTemplateResponse] {

    override def execute(client: RestClient, req: PutSearchTemplateDefinition): Future[PutSearchTemplateResponse] = {

      val method = "POST"
      val endpoint = "/_search/template/" + req.name

      val body = PutSearchTemplateContentBuilder(req).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)
      client.async(method, endpoint, Map.empty, entity, ResponseHandler.default)
    }
  }

  implicit object GetSearchTemplateExecutable
    extends HttpExecutable[GetSearchTemplateDefinition, Option[GetSearchTemplateResponse]] {

    override def execute(client: RestClient, req: GetSearchTemplateDefinition): Future[Option[GetSearchTemplateResponse]] = {
      val method = "GET"
      val endpoint = "/_search/template/" + req.name
      client.async(method, endpoint, Map.empty, new ResponseHandler[Option[GetSearchTemplateResponse]] {
        override def onResponse(response: Response): Try[Option[GetSearchTemplateResponse]] = Try {
          ResponseHandler.fromEntity[GetSearchTemplateResponse](response.getEntity).some
        }
        override def onError(e: Exception): Try[Option[GetSearchTemplateResponse]] = e match {
          case re: ResponseException if re.getResponse.getStatusLine.getStatusCode == 404 => Success(None)
          case _ => Failure(e)
        }
      })
    }
  }
}

case class PutSearchTemplateResponse(acknowledged: Boolean)

case class GetSearchTemplateResponse(@JsonProperty("_id") id: String, lang: String, found: Boolean, template: String)

case class RemoveSearchTemplateResponse(acknowledged: Boolean)
