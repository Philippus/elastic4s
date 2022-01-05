package com.sksamuel.elastic4s.handlers.index

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.analysis.AnalysisBuilder
import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.handlers.index.mapping.MappingBuilderFn
import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexTemplateRequest, DeleteIndexTemplateRequest, GetIndexTemplateRequest, IndexTemplateExistsRequest}
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler}

case class CreateIndexTemplateResponse(acknowledged: Boolean)
case class DeleteIndexTemplateResponse()
case class IndexTemplateExists()

case class GetIndexTemplatesResponse(@JsonProperty("index_templates") indexTemplates: List[Templates])

case class Templates(name: String, @JsonProperty("index_template") template: IndexTemplate)

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
          case _ => Left(ElasticErrorParser.parse(response))
        }
    }

    override def build(request: CreateIndexTemplateRequest): ElasticRequest = {
      val endpoint = "/_index_template/" + request.name
      val body = CreateIndexTemplateBodyFn(request)
      val entity = HttpEntity(body.string(), "application/json")
      ElasticRequest("PUT", endpoint, entity)
    }
  }

  implicit object DeleteIndexTemplateHandler extends Handler[DeleteIndexTemplateRequest, DeleteIndexTemplateResponse] {
    override def build(request: DeleteIndexTemplateRequest): ElasticRequest = {
      val endpoint = "/_index_template/" + request.name
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object GetIndexTemplateHandler extends Handler[GetIndexTemplateRequest, GetIndexTemplatesResponse] {

    override def responseHandler: ResponseHandler[GetIndexTemplatesResponse] = new ResponseHandler[GetIndexTemplatesResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, GetIndexTemplatesResponse] = response.statusCode match {
        case 200 =>
          val templates = ResponseHandler.fromResponse[GetIndexTemplatesResponse](response)
          Right(templates)
        case _ => Left(ElasticErrorParser.parse(response))
      }
    }

    override def build(request: GetIndexTemplateRequest): ElasticRequest = {
      val endpoint = s"/_index_template/" + request.indexes.string(true)
      ElasticRequest("GET", endpoint)
    }
  }
}

object CreateIndexTemplateBodyFn {
  def apply(create: CreateIndexTemplateRequest): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.array("index_patterns", create.pattern.toArray)
    create.order.foreach(builder.field("order", _))
    create.version.foreach(builder.field("version", _))
    create.priority.foreach(builder.field("priority", _))


    if (create.settings.nonEmpty || create.analysis.nonEmpty || create.mappings.nonEmpty) {
      val template = builder.startObject("template")

      if (create.settings.nonEmpty || create.analysis.nonEmpty) {
        template.startObject("settings")
        create.settings.foreach {
          case (key, value) => builder.autofield(key, value)
        }
        create.analysis.foreach(a => builder.rawField("analysis", AnalysisBuilder.build(a)))
        template.endObject()
      }

      if (create.mappings.length == 1) {
        template.rawField("mappings", MappingBuilderFn.build(create.mappings.head))
      }

      if (create.aliases.nonEmpty) {
        template.startObject("aliases")
        create.aliases.foreach { a =>
          builder.startObject(a.name)
          a.routing.foreach(builder.field("routing", _))
          a.filter.foreach { filter =>
            builder.rawField("filter", queries.QueryBuilderFn(filter))
          }
          builder.endObject()
        }
        builder.endObject()
      }
    }

    builder.endObject()
    builder
  }
}
