package com.sksamuel.elastic4s.http.reindex

import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.reindex.ReindexDefinition
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.collection.mutable
import scala.concurrent.Future


trait ReindexImplicits {

  implicit object ReindexDefinitionExecutable extends HttpExecutable[ReindexDefinition, ReindexResponse] {

    override def execute(client: RestClient, request: ReindexDefinition): Future[ReindexResponse] = {
      val endpoint = "/_reindex/"
      val params = mutable.Map.empty[String, Any]

      val body = ReindexContentBuilder(request)
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)
      logger.debug(s"Update Entity: ${body.string}")

      client.async("POST", endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }

}

