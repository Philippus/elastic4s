package com.sksamuel.elastic4s.http.index.alias

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.alias.{GetAliasDefinition, IndicesAliasesRequestDefinition}
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.http.index.IndicesAliasResponse
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{ResponseException, RestClient}

import scala.concurrent.Future
import scala.util.{Failure, Try}
import scala.collection.JavaConverters._

trait IndexAliasImplicits {

  type GetAliasResponse = Map[String, Map[String, Map[String, AnyRef]]]

  implicit object GetAliasExecutable extends HttpExecutable[GetAliasDefinition, GetAliasResponse] {
    override def execute(client: RestClient,
                         request: GetAliasDefinition,
                         format: JsonFormat[GetAliasResponse]): Future[GetAliasResponse] = {
      val indexPathElement = if (request.indices.isEmpty) "" else s"/${request.indices.mkString(",")}"
      val endpoint = s"$indexPathElement/_alias/${request.aliases.mkString(",")}"
      logger.debug(s"Connecting to $endpoint for get alias request")

      executeAsyncAndMapResponse(client.performRequestAsync("GET", endpoint, _), format, {
        case re: ResponseException if re.getResponse.getStatusLine.getStatusCode == 404 => Try(Map.empty)
        case e => Failure(e)
      })
    }
  }

  implicit object IndexAliasesExecutable extends HttpExecutable[IndicesAliasesRequestDefinition, IndicesAliasResponse] {
    override def execute(client: RestClient,
                         request: IndicesAliasesRequestDefinition,
                         format: JsonFormat[IndicesAliasResponse]): Future[IndicesAliasResponse] = {
      val method = "POST"
      val endpoint = "/_aliases"

      val body = AliasActionBuilder(request).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      logger.debug(s"Executing alias actions $body")
      executeAsyncAndMapResponse(client.performRequestAsync(method, endpoint, Map.empty[String, String].asJava, entity, _), format)
    }
  }
}
