package com.sksamuel.elastic4s.http.index.alias

import com.sksamuel.elastic4s.alias.{GetAliasDefinition, GetAliasesDefinition, IndicesAliasesRequestDefinition}
import com.sksamuel.elastic4s.http.index.admin.IndicesAliasResponse
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, ResponseException, RestClient}

import scala.concurrent.Future
import scala.util.{Failure, Try}

trait IndexAliasImplicits {

  type GetAliasResponse = Map[String, Map[String, Map[String, AnyRef]]]

  implicit object GetAliasesDefinition extends HttpExecutable[GetAliasesDefinition, Seq[Alias]] {
    override def execute(client: RestClient, request: GetAliasesDefinition): Future[Seq[Alias]] = {
      client.async("GET", "/_aliases", Map.empty, new ResponseHandler[Seq[Alias]] {
        override def onResponse(response: Response): Try[Seq[Alias]] = Try {
          ResponseHandler.fromEntity[Map[String, Map[String, Map[String, AnyRef]]]](response.getEntity).map { case (name, map) =>
            Alias(name, map.getOrElse("aliases", Map.empty).keySet.toSeq)
          }.toSeq
        }
      })
    }
  }

  implicit object GetAliasExecutable extends HttpExecutable[GetAliasDefinition, GetAliasResponse] {
    override def execute(client: RestClient,
                         request: GetAliasDefinition): Future[GetAliasResponse] = {

      val indexPathElement = if (request.indices.isEmpty) "" else s"/${request.indices.mkString(",")}"
      val endpoint = s"$indexPathElement/_alias/${request.aliases.mkString(",")}"

      client.async("GET", endpoint, Map.empty, new ResponseHandler[GetAliasResponse] {
        override def onError(ex: Exception): Try[Map[String, Nothing]] = ex match {
          case re: ResponseException if re.getResponse.getStatusLine.getStatusCode == 404 => Try(Map.empty)
          case e => Failure(e)
        }
        override def onResponse(response: Response): Try[GetAliasResponse] = Try {
          ResponseHandler.fromEntity[GetAliasResponse](response.getEntity)
        }
      })
    }
  }

  implicit object IndexAliasesExecutable extends HttpExecutable[IndicesAliasesRequestDefinition, IndicesAliasResponse] {
    override def execute(client: RestClient, request: IndicesAliasesRequestDefinition): Future[IndicesAliasResponse] = {
      val body = AliasActionBuilder(request).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)
      client.async("POST", "/_aliases", Map.empty, entity, ResponseHandler.default)
    }
  }
}

case class Alias(name: String, indexes: Seq[String])
