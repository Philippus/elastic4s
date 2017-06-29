package com.sksamuel.elastic4s.http.index

import cats.Show
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.indexes._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.mappings.MappingBuilderFn
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, ResponseListener, RestClient}

import scala.concurrent.Future

case class CreateIndexTemplateResponse()
case class DeleteIndexTemplateResponse()
case class GetIndexTemplateResponse()

trait IndexTemplateImplicits {

  implicit object CreateIndexTemplateHttpExecutable extends HttpExecutable[CreateIndexTemplateDefinition, CreateIndexTemplateResponse] {
    override def execute(client: RestClient,
                         request: CreateIndexTemplateDefinition): Future[Response] = {
      val endpoint = s"/_template/" + request.name
      val body = CreateIndexTemplateBodyFn(request)
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)
      client.async("PUT", endpoint, Map.empty, entity)
    }
  }

  implicit object DeleteIndexTemplateHttpExecutable extends HttpExecutable[DeleteIndexTemplateDefinition, DeleteIndexTemplateResponse] {
    override def execute(client: RestClient,
                         request: DeleteIndexTemplateDefinition): Future[Response] = {
      val endpoint = s"/_template/" + request.name
      client.async("DELETE", endpoint, Map.empty)
    }
  }

  implicit object GetIndexTemplateHttpExecutable extends HttpExecutable[GetIndexTemplateDefinition, GetIndexTemplateResponse] {
    override def execute(client: RestClient,
                         request: GetIndexTemplateDefinition): Future[Response] = {
      val endpoint = s"/_template/" + request.name
      val fn = client.performRequestAsync("GET", endpoint, _: ResponseListener)
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
        builder.rawValue(MappingBuilderFn.buildWithName(mapping, mapping.`type`))
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
