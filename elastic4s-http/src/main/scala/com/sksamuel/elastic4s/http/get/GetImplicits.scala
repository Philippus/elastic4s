package com.sksamuel.elastic4s.http.get

import cats.Show
import com.sksamuel.elastic4s.get.{GetDefinition, MultiGetDefinition}
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.exts.Logging
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

import scala.collection.JavaConverters._

case class MultiGetResponse(docs: Seq[GetResponse]) {
  def items = docs
  def size = docs.size
}

object MultiGetBodyBuilder {
  def apply(request: MultiGetDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startArray("docs")
    request.gets.foreach { get =>
      builder.startObject()
      builder.field("_index", get.indexAndType.index)
      builder.field("_type", get.indexAndType.`type`)
      builder.field("_id", get.id)
      get.fetchSource.foreach { context =>
        if (context.includes.nonEmpty || context.excludes.nonEmpty) {
          builder.startObject("_source")
          if (context.includes.nonEmpty)
            builder.field("include", context.includes)
          if (context.excludes.nonEmpty)
            builder.field("exclude", context.excludes)
          builder.endObject()
        } else {
          builder.field("_source", false)
        }
      }
      if (get.storedFields.nonEmpty) {
        builder.field("stored_fields", get.storedFields.toArray)
      }
      builder.endObject()
    }
    builder.endArray()
    builder.endObject()
    builder
  }
}

trait GetImplicits {

  implicit object MultiGetShow extends Show[MultiGetDefinition] {
    override def show(f: MultiGetDefinition): String = MultiGetBodyBuilder(f).string()
  }

  implicit object MultiGetHttpExecutable extends HttpExecutable[MultiGetDefinition, MultiGetResponse] with Logging {
    override def execute(client: RestClient, request: MultiGetDefinition): (ResponseListener) => Any = {

      val body = MultiGetBodyBuilder(request).string()
      logger.debug(s"Executing multiget $body")
      val entity = new StringEntity(body)

      val params = scala.collection.mutable.Map.empty[String, String]

      client.performRequestAsync("POST", "/_mget", params.asJava, entity, _)
    }
  }

  implicit object GetHttpExecutable extends HttpExecutable[GetDefinition, GetResponse] with Logging {

    override def execute(client: RestClient, request: GetDefinition): ResponseListener => Any = {

      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fetchSource.foreach { context =>
        if (!context.fetchSource)
          params.put("_source", "false")
      }
      if (request.storedFields.nonEmpty) {
        params.put("stored_fields", request.storedFields.mkString(","))
      }
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.preference.foreach(params.put("preference", _))
      request.refresh.map(_.toString).foreach(params.put("refresh", _))
      request.realtime.map(_.toString).foreach(params.put("realtime", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.foreach(params.put("versionType", _))

      client.performRequestAsync("GET", endpoint, params.asJava, _: ResponseListener)
    }
  }
}
