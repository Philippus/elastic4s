package com.sksamuel.elastic4s.http.snapshots

import com.sksamuel.elastic4s.http.{HttpEntity, HttpExecutable, HttpRequestClient, HttpResponse}
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.repository.CreateRepositoryDefinition
import org.apache.http.entity.ContentType

import scala.concurrent.Future

case class CreateRepositoryResponse()

trait SnapshotHttpImplicits {

  implicit object CreateRepositoryHttpExecutable extends HttpExecutable[CreateRepositoryDefinition, CreateRepositoryResponse] {

    override def execute(client: HttpRequestClient, request: CreateRepositoryDefinition): Future[HttpResponse] = {

      val endpoint = s"/_snapshot/" + request.name

      val params = scala.collection.mutable.Map.empty[String, String]
      request.verify.map(_.toString).foreach(params.put("verify", _))

      val body = XContentFactory.jsonBuilder()
      body.field("type", request.`type`)
      body.startObject("settings")
      request.settings.foreach { case (key, value) =>
        body.field(key, value.toString)
      }
      body.endObject()
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)

      client.async("PUT", endpoint, params.toMap, entity)
    }
  }
}
