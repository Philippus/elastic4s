package com.sksamuel.elastic4s.http.index

import cats.Show
import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.indexes._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.mappings.MappingBuilderFn
import org.apache.http.entity.ContentType

case class CreateIndexTemplateResponse(acknowledged: Boolean)
case class DeleteIndexTemplateResponse()
case class IndexTemplateExists()

case class GetIndexTemplates(templates: Map[String, IndexTemplate]) {
  def templateFor(name: String): IndexTemplate = templates(name)
}

case class IndexTemplate(order: Int,
                         @JsonProperty("index_patterns") indexPatterns: Seq[String],
                         settings: Map[String, String],
                         mappings: Map[String, Any],
                         aliases: Map[String, Any])

trait IndexTemplateImplicits {

  implicit object IndexTemplateExistsHttpExecutable extends HttpExecutable[IndexTemplateExistsDefinition, IndexTemplateExists] {
    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: IndexTemplateExistsDefinition): F[HttpResponse] = ???
  }

  implicit object CreateIndexTemplateHttpExecutable extends HttpExecutable[CreateIndexTemplateDefinition, CreateIndexTemplateResponse] {

    override def responseHandler = new ResponseHandler[CreateIndexTemplateResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, CreateIndexTemplateResponse] = response.statusCode match {
        case 200 => Right(ResponseHandler.fromResponse[CreateIndexTemplateResponse](response))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient,
                         request: CreateIndexTemplateDefinition): F[HttpResponse] = {
      val endpoint = s"/_template/" + request.name
      val body = CreateIndexTemplateBodyFn(request)
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)
      client.async("PUT", endpoint, Map.empty, entity)
    }
  }

  implicit object DeleteIndexTemplateHttpExecutable extends HttpExecutable[DeleteIndexTemplateDefinition, DeleteIndexTemplateResponse] {
    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient,
                         request: DeleteIndexTemplateDefinition): F[HttpResponse] = {
      val endpoint = s"/_template/" + request.name
      client.async("DELETE", endpoint, Map.empty)
    }
  }

  implicit object GetIndexTemplateHttpExecutable extends HttpExecutable[GetIndexTemplateDefinition, GetIndexTemplates] {

    override def responseHandler = new ResponseHandler[GetIndexTemplates] {
      override def handle(response: HttpResponse): Either[ElasticError, GetIndexTemplates] = response.statusCode match {
        case 200 =>
          val templates = ResponseHandler.fromResponse[Map[String, IndexTemplate]](response)
          Right(GetIndexTemplates(templates))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: GetIndexTemplateDefinition): F[HttpResponse] = {
      val endpoint = s"/_template/" + request.indexes.string
      client.async("GET", endpoint, Map.empty)
    }
  }

  implicit object CreateIndexTemplateShow extends Show[CreateIndexTemplateDefinition] {
    override def show(req: CreateIndexTemplateDefinition): String = CreateIndexTemplateBodyFn(req).string()
  }
}

object CreateIndexTemplateBodyFn {
  def apply(create: CreateIndexTemplateDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("template", create.pattern)
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
