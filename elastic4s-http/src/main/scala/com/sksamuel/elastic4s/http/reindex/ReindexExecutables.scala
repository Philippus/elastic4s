package com.sksamuel.elastic4s.http.reindex

import com.sksamuel.elastic4s.http.nodes.NodeInfoResponse
import com.sksamuel.elastic4s.http.update.UpdateContentBuilder
import com.sksamuel.elastic4s.http.{HttpClient, HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.reindex.ReindexDefinition
import org.apache.http.entity.{ContentType, StringEntity}

import scala.collection.mutable
import scala.concurrent.Future


case class ReindexRequest()

case class ReindexResponse()

trait ReindexExecutables {

  implicit object ReindexDefinitionExecutable extends HttpExecutable[ReindexRequest, ReindexResponse] {

    override def execute(client: HttpClient, request: ReindexDefinition): Future[NodeInfoResponse] = {
      val endpoint = "/_reindex/"
      val params = mutable.Map.empty[String, Any]

      val source = mutable.Map.empty[String, Any]
      source.put("index", request.sourceIndexes.values)
      params.put("source", source)
      request.size.foreach(params.put("size", _))

      val target = mutable.Map.empty[String, Any]
      request.targetIndex.foreach(target.put("index", _))
      request.targetType.foreach(target.put("type", _))
      request.script.foreach(params.put("script", _))
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))

      val body = UpdateContentBuilder(request)
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)
      logger.debug(s"Update Entity: ${body.string}")

      client.async("POST", endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }

}

