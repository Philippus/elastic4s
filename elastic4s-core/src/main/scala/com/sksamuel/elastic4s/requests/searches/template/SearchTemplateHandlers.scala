package com.sksamuel.elastic4s.requests.searches.template

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.searches.{GetSearchTemplateRequest, PutSearchTemplateRequest, RemoveSearchTemplateRequest, SearchResponse, TemplateSearchRequest}
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity, HttpResponse, IndexesAndTypes, ResponseHandler}
import com.sksamuel.exts.OptionImplicits._

trait SearchTemplateHandlers {

  implicit object TemplateSearchHandler extends Handler[TemplateSearchRequest, SearchResponse] {

    override def build(req: TemplateSearchRequest): ElasticRequest = {
      val endpoint = req.indexesAndTypes match {
        case IndexesAndTypes(Nil, Nil)     => "/_search/template"
        case IndexesAndTypes(indexes, Nil) => "/" + indexes.mkString(",") + "/_search/template"
        case IndexesAndTypes(Nil, types)   => "/_all/" + types.mkString(",") + "/_search/template"
        case IndexesAndTypes(indexes, types) =>
          "/" + indexes.mkString(",") + "/" + types.mkString(",") + "/_search/template"
      }
      val body = TemplateSearchBuilderFn(req).string()
      ElasticRequest("POST", endpoint, HttpEntity(body, "application/json"))
    }
  }

  implicit object RemoveSearchTemplateHandler
      extends Handler[RemoveSearchTemplateRequest, RemoveSearchTemplateResponse] {

    override def build(req: RemoveSearchTemplateRequest): ElasticRequest = {
      val endpoint = "/_scripts/" + req.name
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object PutSearchTemplateHandler extends Handler[PutSearchTemplateRequest, PutSearchTemplateResponse] {

    override def build(req: PutSearchTemplateRequest): ElasticRequest = {
      val endpoint = "/_scripts/" + req.name
      val body     = PutSearchTemplateBuilderFn(req).string()
      val entity   = HttpEntity(body, "application/json")
      ElasticRequest("POST", endpoint, entity)
    }
  }

  implicit object GetSearchTemplateHandler
      extends Handler[GetSearchTemplateRequest, Option[GetSearchTemplateResponse]] {

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
            case _   => sys.error(response.entity.map(_.content).getOrElse(""))
          }
      }

    override def build(req: GetSearchTemplateRequest): ElasticRequest = {
      val endpoint = "/_scripts/" + req.name
      ElasticRequest("GET", endpoint)
    }
  }
}

case class PutSearchTemplateResponse(acknowledged: Boolean)

case class GetSearchTemplateResponse(@JsonProperty("_id") id: String, lang: String, found: Boolean, template: String)

case class RemoveSearchTemplateResponse(acknowledged: Boolean)
