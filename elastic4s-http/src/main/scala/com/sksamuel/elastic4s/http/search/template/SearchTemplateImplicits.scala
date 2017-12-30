package com.sksamuel.elastic4s.http.search.template

import cats.Functor
import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.searches.{GetSearchTemplateDefinition, PutSearchTemplateDefinition, RemoveSearchTemplateDefinition, TemplateSearchDefinition}
import com.sksamuel.exts.OptionImplicits._
import org.apache.http.entity.ContentType

trait SearchTemplateImplicits {

  implicit object TemplateSearchExecutable
    extends HttpExecutable[TemplateSearchDefinition, SearchResponse] {

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, req: TemplateSearchDefinition): F[HttpResponse] = {
      val endpoint = req.indexesAndTypes match {
        case IndexesAndTypes(Nil, Nil) => "/_search/template"
        case IndexesAndTypes(indexes, Nil) => "/" + indexes.mkString(",") + "/_search/template"
        case IndexesAndTypes(Nil, types) => "/_all/" + types.mkString(",") + "/_search/template"
        case IndexesAndTypes(indexes, types) => "/" + indexes.mkString(",") + "/" + types.mkString(",") + "/_search/template"
      }
      val body = TemplateSearchBuilderFn(req).string()
      client.async("POST", endpoint, Map.empty, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
    }
  }

  implicit object RemoveSearchTemplateExecutable
    extends HttpExecutable[RemoveSearchTemplateDefinition, RemoveSearchTemplateResponse] {

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, req: RemoveSearchTemplateDefinition): F[HttpResponse] = {
      val endpoint = "/_scripts/" + req.name
      client.async("DELETE", endpoint, Map.empty)
    }
  }

  implicit object PutSearchTemplateExecutable
    extends HttpExecutable[PutSearchTemplateDefinition, PutSearchTemplateResponse] {

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, req: PutSearchTemplateDefinition): F[HttpResponse] = {
      val endpoint = "/_scripts/" + req.name
      val body = PutSearchTemplateBuilderFn(req).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)
      client.async("POST", endpoint, Map.empty, entity)
    }
  }

  implicit object GetSearchTemplateExecutable
    extends HttpExecutable[GetSearchTemplateDefinition, Option[GetSearchTemplateResponse]] {

    override def responseHandler: ResponseHandler[Option[GetSearchTemplateResponse]] = new ResponseHandler[Option[GetSearchTemplateResponse]] {
      /**
        * Accepts a HttpResponse and returns an Either of an ElasticError or a type specific to the request
        * as determined by the instance of this handler.
        */
      override def handle(response: HttpResponse) = {
        response.statusCode match {
          case 200 => Right(ResponseHandler.fromResponse[GetSearchTemplateResponse](response).some)
          case 404 => Right(None)
          case _ => sys.error(response.entity.map(_.content).getOrElse(""))
        }
      }
    }

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, req: GetSearchTemplateDefinition): F[HttpResponse] = {
      val endpoint = "/_scripts/" + req.name
      client.async("GET", endpoint, Map.empty)
    }
  }
}

case class PutSearchTemplateResponse(acknowledged: Boolean)

case class GetSearchTemplateResponse(@JsonProperty("_id") id: String, lang: String, found: Boolean, template: String)

case class RemoveSearchTemplateResponse(acknowledged: Boolean)
