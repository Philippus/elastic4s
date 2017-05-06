package com.sksamuel.elastic4s.http.index.alias

import com.sksamuel.elastic4s.alias.{GetAliasDefinition, IndicesAliasesRequestDefinition}
import com.sksamuel.elastic4s.http.index.IndicesAliasResponse
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, ResponseException, RestClient}

import scala.concurrent.Future
import scala.util.{Failure, Try}

trait IndexAliasImplicits {

  type GetAliasResponse = Map[String, Map[String, Map[String, AnyRef]]]

  implicit object GetAliasExecutable extends HttpExecutable[GetAliasDefinition, GetAliasResponse] {
    override def execute(client: RestClient,
                         request: GetAliasDefinition): Future[GetAliasResponse] = {
      val indexPathElement = if (request.indices.isEmpty) "" else s"/${request.indices.mkString(",")}"
      val endpoint = s"$indexPathElement/_alias/${request.aliases.mkString(",")}"
      logger.debug(s"Connecting to $endpoint for get alias request")

      client.async("GET", endpoint, Map.empty, new ResponseHandler[GetAliasResponse] {
        override def onError(e: Exception) = e match {
          case re: ResponseException if re.getResponse.getStatusLine.getStatusCode == 404 => Try(Map.empty)
          case e => Failure(e)
        }
        override def onResponse(response: Response) = ResponseHandler.fromEntity[GetAliasResponse](response.getEntity)
      })
    }
  }

  implicit object IndexAliasesExecutable extends HttpExecutable[IndicesAliasesRequestDefinition, IndicesAliasResponse] {
    override def execute(client: RestClient,
                         request: IndicesAliasesRequestDefinition): Future[IndicesAliasResponse] = {
      val method = "POST"
      val endpoint = "/_aliases"

      val body = AliasActionBuilder(request).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      logger.debug(s"Executing alias actions $body")
      client.async(method, endpoint, Map.empty, entity, ResponseHandler.default)
    }
  }
}
