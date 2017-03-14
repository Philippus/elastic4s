package com.sksamuel.elastic4s.http.alias

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.alias.GetAliasDefinition
import com.sksamuel.elastic4s.http.HttpExecutable
import org.elasticsearch.client.{ResponseException, RestClient}

import scala.concurrent.Future
import scala.util.{Failure, Try}

trait AliasImplicits {

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

}
