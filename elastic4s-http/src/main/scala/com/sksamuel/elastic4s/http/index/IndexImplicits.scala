package com.sksamuel.elastic4s.http.index

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import com.sksamuel.elastic4s.http.{HttpExecutable, RefreshPolicyHttpValue, ResponseHandler}
import com.sksamuel.elastic4s.indexes.{IndexContentBuilder, IndexDefinition, IndexShowImplicits}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

trait IndexImplicits extends IndexShowImplicits {

  implicit object IndexHttpExecutable extends HttpExecutable[IndexDefinition, IndexResponse] {

    override def execute(client: RestClient,
                         request: IndexDefinition): Future[IndexResponse] = {

      val (method, endpoint) = request.id match {
        case Some(id) =>
          "PUT" -> s"/${URLEncoder.encode(request.indexAndType.index, StandardCharsets.UTF_8.name())}/${URLEncoder
            .encode(request.indexAndType.`type`, StandardCharsets.UTF_8.name())}/${URLEncoder.encode(id.toString, StandardCharsets.UTF_8.name())}"
        case None =>
          "POST" -> s"/${URLEncoder.encode(request.indexAndType.index, StandardCharsets.UTF_8.name())}/${URLEncoder
            .encode(request.indexAndType.`type`, StandardCharsets.UTF_8.name())}"
      }

      val params = scala.collection.mutable.Map.empty[String, String]
      request.createOnly.foreach(createOnly =>
        if(createOnly) {
          params.put("op_type", "create")
        }
      )
      request.routing.foreach(params.put("routing", _))
      request.parent.foreach(params.put("parent", _))
      request.timeout.foreach(params.put("timeout", _))
      request.pipeline.foreach(params.put("pipeline", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.map(VersionTypeHttpString.apply).foreach(params.put("version_type", _))

      val body = IndexContentBuilder(request)
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)

      logger.debug(s"Endpoint=$endpoint")
      client.async(method, endpoint, params.toMap, entity, ResponseHandler.failure404)
    }
  }
}


