package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.indexes.{CreateIndexTemplateDefinition, DeleteIndexTemplateDefinition, GetIndexTemplateDefinition}
import com.sksamuel.elastic4s.mappings.MappingContentBuilder
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{ResponseListener, RestClient}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class CreateIndexTemplateResponse()
case class DeleteIndexTemplateResponse()
case class GetIndexTemplateResponse()

trait IndexTemplateImplicits {

  implicit object CreateIndexTemplateHttpExecutable extends HttpExecutable[CreateIndexTemplateDefinition, CreateIndexTemplateResponse] {
    override def execute(client: RestClient,
                         request: CreateIndexTemplateDefinition): Future[CreateIndexTemplateResponse] = {
      val endpoint = s"/_template/" + request.name
      val body = CreateIndexTemplateBodyFn(request)
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)
      client.async("PUT", endpoint, Map.empty, entity, ResponseHandler.default)
    }
  }

  implicit object DeleteIndexTemplateHttpExecutable extends HttpExecutable[DeleteIndexTemplateDefinition, DeleteIndexTemplateResponse] {
    override def execute(client: RestClient,
                         request: DeleteIndexTemplateDefinition): Future[DeleteIndexTemplateResponse] = {
      val endpoint = s"/_template/" + request.name
      client.async("DELETE", endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object GetIndexTemplateHttpExecutable extends HttpExecutable[GetIndexTemplateDefinition, GetIndexTemplateResponse] {
    override def execute(client: RestClient,
                         request: GetIndexTemplateDefinition): Future[GetIndexTemplateResponse] = {
      val endpoint = s"/_template/" + request.name
      val fn = client.performRequestAsync("GET", endpoint, _: ResponseListener)
      client.async("GET", endpoint, Map.empty, ResponseHandler.default)
    }
  }
}

object CreateIndexTemplateBodyFn {
  def apply(create: CreateIndexTemplateDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.field("template", create.pattern)
    create.order.foreach(builder.field("order", _))
    create.version.foreach(builder.field("version", _))

    if (create.settings.getAsMap.size > 0) {
      builder.startObject("settings")
      create.settings.getAsMap.asScala.foreach {
        case (key, value) => builder.field(key, value)
      }
      builder.endObject()
    }

    if (create.mappings.nonEmpty) {
      builder.startObject("mappings")
      create.mappings.foreach { mapping =>
        builder.rawValue(MappingContentBuilder.buildWithName(mapping, mapping.`type`).bytes, XContentType.JSON)
      }
      builder.endObject()
    }

    if (create.alias.nonEmpty) {
      builder.startObject("aliases")
      create.alias.foreach { a =>
        builder.startObject(a.name)
        a.routing.foreach(builder.field("routing", _))
        a.filter.foreach { filter =>
          builder.rawField("filter", QueryBuilderFn(filter).bytes, XContentType.JSON)
        }
        builder.endObject()
      }
      builder.endObject()
    }

    builder.endObject()
    builder
  }
}
