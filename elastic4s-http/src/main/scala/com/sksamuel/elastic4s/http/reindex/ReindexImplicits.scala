package com.sksamuel.elastic4s.http.reindex

import com.sksamuel.elastic4s.http.{EncodeURLParameters, HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.reindex.ReindexDefinition
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.collection.mutable
import scala.concurrent.Future


trait ReindexImplicits {

  implicit object ReindexDefinitionHttpExecutable extends HttpExecutable[ReindexDefinition, ReindexResponse] {

    override def execute(client: RestClient, request: ReindexDefinition): Future[ReindexResponse] = {
      val endpoint = s"/_reindex${EncodeURLParameters(request.urlParams)}"

      val params = mutable.Map.empty[String, Any]

      val body = ReindexContentBuilder(request)
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)
      logger.debug(s"Reindex entity: ${body.string}")

      client.asyncTimeout("POST", endpoint, params.toMap, entity, ResponseHandler.default, request.urlParams.timeout)
    }
  }

}

