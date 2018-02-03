package com.sksamuel.elastic4s.http.search.template

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.searches.{GetSearchTemplateDefinition, PutSearchTemplateDefinition, RemoveSearchTemplateDefinition, TemplateSearchDefinition}
import com.sksamuel.exts.OptionImplicits._
import org.apache.http.entity.ContentType

trait SearchTemplateImplicits {

  implicit object TemplateSearchExecutable extends Handler[TemplateSearchDefinition, SearchResponse] {

    override def requestHandler(req: TemplateSearchDefinition): ElasticRequest = {
      val endpoint = req.indexesAndTypes match {
        case IndexesAndTypes(Nil, Nil) => "/_search/template"
        case IndexesAndTypes(indexes, Nil) => "/" + indexes.mkString(",") + "/_search/template"
        case IndexesAndTypes(Nil, types) => "/_all/" + types.mkString(",") + "/_search/template"
        case IndexesAndTypes(indexes, types) =>
          "/" + indexes.mkString(",") + "/" + types.mkString(",") + "/_search/template"
      }
      val body = TemplateSearchBuilderFn(req).string()
      ElasticRequest("POST", endpoint, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
    }
  }

  implicit object RemoveSearchTemplateExecutable
    extends Handler[RemoveSearchTemplateDefinition, RemoveSearchTemplateResponse] {

    override def requestHandler(req: RemoveSearchTemplateDefinition): ElasticRequest = {
      val endpoint = "/_scripts/" + req.name
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object PutSearchTemplateExecutable
    extends Handler[PutSearchTemplateDefinition, PutSearchTemplateResponse] {

    override def requestHandler(req: PutSearchTemplateDefinition): ElasticRequest = {
      val endpoint = "/_scripts/" + req.name
      val body = PutSearchTemplateBuilderFn(req).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)
      ElasticRequest("POST", endpoint, entity)
    }
  }

  implicit object GetSearchTemplateExecutable
    extends Handler[GetSearchTemplateDefinition, Option[GetSearchTemplateResponse]] {

    override def responseHandler: ResponseHandler[Option[GetSearchTemplateResponse]] =
      new ResponseHandler[Option[GetSearchTemplateResponse]] {

        /**
          * Accepts a HttpResponse and returns an Either of an ElasticError or a type specific to the request
          * as determined by the instance of this handler.
          */
        override def handle(response: HttpResponse) =
          response.statusCode match {
            case 200 => Right(ResponseHandler.fromResponse[GetSearchTemplateResponse](response).some)
            case 404 => Right(None)
            case _ => sys.error(response.entity.map(_.content).getOrElse(""))
          }
      }

    override def requestHandler(req: GetSearchTemplateDefinition): ElasticRequest = {
      val endpoint = "/_scripts/" + req.name
      ElasticRequest("GET", endpoint)
    }
  }
}

case class PutSearchTemplateResponse(acknowledged: Boolean)

case class GetSearchTemplateResponse(@JsonProperty("_id") id: String, lang: String, found: Boolean, template: String)

case class RemoveSearchTemplateResponse(acknowledged: Boolean)
