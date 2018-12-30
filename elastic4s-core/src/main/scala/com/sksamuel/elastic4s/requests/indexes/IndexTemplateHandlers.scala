package com.sksamuel.elastic4s.requests.indexes

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.mappings.MappingBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler, XContentBuilder, XContentFactory}

case class CreateIndexTemplateResponse(acknowledged: Boolean)
case class DeleteIndexTemplateResponse()
case class IndexTemplateExists()

case class GetIndexTemplates(templates: Map[String, IndexTemplate]) {
  def templateFor(name: String): IndexTemplate = templates(name)
}

case class IndexTemplate(order: Int,
                         @JsonProperty("index_patterns") indexPatterns: Seq[String],
                         settings: Map[String, Any],
                         mappings: Map[String, Any],
                         aliases: Map[String, Any])

trait IndexTemplateHandlers {

  implicit object IndexTemplateExistsHandler extends Handler[IndexTemplateExistsRequest, IndexTemplateExists] {
    override def build(request: IndexTemplateExistsRequest): ElasticRequest = ???
  }

  implicit object CreateIndexTemplateHandler extends Handler[CreateIndexTemplateRequest, CreateIndexTemplateResponse] {

    override def responseHandler: ResponseHandler[CreateIndexTemplateResponse] = new ResponseHandler[CreateIndexTemplateResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, CreateIndexTemplateResponse] =
        response.statusCode match {
          case 200 => Right(ResponseHandler.fromResponse[CreateIndexTemplateResponse](response))
          case _   => Left(ElasticError.parse(response))
        }
    }

    override def build(request: CreateIndexTemplateRequest): ElasticRequest = {
      val endpoint = s"/_template/" + request.name
      val body     = CreateIndexTemplateBodyFn(request)
      val entity   = HttpEntity(body.string, "application/json")
      ElasticRequest("PUT", endpoint, entity)
    }
  }

  implicit object DeleteIndexTemplateHandler extends Handler[DeleteIndexTemplateRequest, DeleteIndexTemplateResponse] {
    override def build(request: DeleteIndexTemplateRequest): ElasticRequest = {
      val endpoint = s"/_template/" + request.name
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object GetIndexTemplateHandler extends Handler[GetIndexTemplateRequest, GetIndexTemplates] {

    override def responseHandler: ResponseHandler[GetIndexTemplates] = new ResponseHandler[GetIndexTemplates] {
      override def handle(response: HttpResponse): Either[ElasticError, GetIndexTemplates] = response.statusCode match {
        case 200 =>
          val templates = ResponseHandler.fromResponse[Map[String, IndexTemplate]](response)
          Right(GetIndexTemplates(templates))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def build(request: GetIndexTemplateRequest): ElasticRequest = {
      val endpoint = s"/_template/" + request.indexes.string
      ElasticRequest("GET", endpoint)
    }
  }
}

object CreateIndexTemplateBodyFn {
  def apply(create: CreateIndexTemplateRequest): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.array("index_patterns", Array(create.pattern))
    create.order.foreach(builder.field("order", _))
    create.version.foreach(builder.field("version", _))

    if (create.settings.nonEmpty || create.analysis.nonEmpty) {
      builder.startObject("settings")
      create.settings.foreach {
        case (key, value) => builder.autofield(key, value)
      }
      create.analysis.foreach { analysis =>
        AnalysisBuilderFn.build(analysis, builder)
      }
      builder.endObject()
    }

    if (create.mappings.nonEmpty) {
      builder.startObject("mappings")
      create.mappings.foreach { mapping =>
        builder.rawField(mapping.`type`, MappingBuilderFn.build(mapping))
      }
      builder.endObject()
    }

    if (create.aliases.nonEmpty) {
      builder.startObject("aliases")
      create.aliases.foreach { a =>
        builder.startObject(a.name)
        a.routing.foreach(builder.field("routing", _))
        a.filter.foreach { filter =>
          builder.rawField("filter", QueryBuilderFn(filter))
        }
        builder.endObject()
      }
      builder.endObject()
    }

    builder.endObject()
    builder
  }
}
