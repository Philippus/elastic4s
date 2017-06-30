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

    override def execute(client: RestClient, req: TemplateSearchDefinition): Future[Response] = {
      val endpoint = req.indexesAndTypes match {
        case IndexesAndTypes(Nil, Nil) => "/_search/template"
        case IndexesAndTypes(indexes, Nil) => "/" + indexes.mkString(",") + "/_search/template"
        case IndexesAndTypes(Nil, types) => "/_all/" + types.mkString(",") + "/_search/template"
        case IndexesAndTypes(indexes, types) => "/" + indexes.mkString(",") + "/" + types.mkString(",") + "/_search/template"
      }
      val body = TemplateSearchBuilderFn(req).string()
      client.async("POST", endpoint, Map.empty, new StringEntity(body, ContentType.APPLICATION_JSON))
    }
  }

  implicit object RemoveSearchTemplateExecutable
    extends HttpExecutable[RemoveSearchTemplateDefinition, RemoveSearchTemplateResponse] {

    override def execute(client: RestClient, req: RemoveSearchTemplateDefinition): Future[Response] = {
      val endpoint = "/_search/template/" + req.name
      client.async("DELETE", endpoint, Map.empty)
    }
  }

  implicit object PutSearchTemplateExecutable
    extends HttpExecutable[PutSearchTemplateDefinition, PutSearchTemplateResponse] {

    override def execute(client: RestClient, req: PutSearchTemplateDefinition): Future[Response] = {
      val endpoint = "/_search/template/" + req.name
      val body = PutSearchTemplateBuilderFn(req).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)
      client.async("POST", endpoint, Map.empty, entity)
    }
  }

  implicit object GetSearchTemplateExecutable
    extends HttpExecutable[GetSearchTemplateDefinition, Option[GetSearchTemplateResponse]] {

    override def responseHandler: ResponseHandler[Option[GetSearchTemplateResponse]] = new ResponseHandler[Option[GetSearchTemplateResponse]] {
      override def onResponse(response: Response): Try[Option[GetSearchTemplateResponse]] = Try {
        ResponseHandler.fromEntity[GetSearchTemplateResponse](response.getEntity).some
      }
      override def onError(e: Throwable): Try[Option[GetSearchTemplateResponse]] = e match {
        case re: ResponseException if re.getResponse.getStatusLine.getStatusCode == 404 => Success(None)
        case _ => Failure(e)
      }
    }

    override def execute(client: RestClient, req: GetSearchTemplateDefinition): Future[Response] = {
      val endpoint = "/_search/template/" + req.name
      client.async("GET", endpoint, Map.empty)
    }
  }
}

case class PutSearchTemplateResponse(acknowledged: Boolean)

case class GetSearchTemplateResponse(@JsonProperty("_id") id: String, lang: String, found: Boolean, template: String)

case class RemoveSearchTemplateResponse(acknowledged: Boolean)
