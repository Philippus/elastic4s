package com.sksamuel.elastic4s.http.index.alias

import com.sksamuel.elastic4s.alias.{AddAliasActionDefinition, GetAliasDefinition, GetAliasesDefinition, IndicesAliasesRequestDefinition, RemoveAliasActionDefinition}
import com.sksamuel.elastic4s.http.index.admin.IndicesAliasResponse
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, ResponseException, RestClient}

import scala.concurrent.Future
import scala.util.Try

trait IndexAliasImplicits {

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

  implicit object GetAliasExecutable extends HttpExecutable[GetAliasDefinition, Option[Alias]] {
    override def execute(client: RestClient,
                         request: GetAliasDefinition): Future[Option[Alias]] = {

      val indexPathElement = if (request.indices.isEmpty) "" else s"/${request.indices.mkString(",")}"
      val endpoint = s"$indexPathElement/_alias/${request.aliases.mkString(",")}"
      val params = request.ignoreUnavailable.fold(Map.empty[String, Any]) { ignore => Map("ignore_unavailable" -> ignore) }

      client.async("GET", endpoint, params, new ResponseHandler[Option[Alias]] {

        import scala.collection.JavaConverters._

        override def onError(e: Exception): Try[Option[Alias]] = e match {
          case r: ResponseException if r.getResponse.getStatusLine.getStatusCode == 404 => Try(None)
          case _ => super.onError(e)
        }

        override def onResponse(response: Response): Try[Option[Alias]] = Try {
          val root = ResponseHandler.json(response.getEntity)
          root.fields.asScala.toVector.headOption.map { entry =>
            val aliases = entry.getValue.findValue("aliases").fieldNames().asScala.toList
            Alias(entry.getKey, aliases)
          }
        }
      })
    }
  }

  implicit object RemoveAliasActionExecutable extends HttpExecutable[RemoveAliasActionDefinition, IndicesAliasResponse] {
    override def execute(client: RestClient, request: RemoveAliasActionDefinition): Future[IndicesAliasResponse] = {
      val container = IndicesAliasesRequestDefinition(Seq(request))
      IndexAliasesExecutable.execute(client, container)
    }
  }

  implicit object AddAliasActionExecutable extends HttpExecutable[AddAliasActionDefinition, IndicesAliasResponse] {
    override def execute(client: RestClient, request: AddAliasActionDefinition): Future[IndicesAliasResponse] = {
      val container = IndicesAliasesRequestDefinition(Seq(request))
      IndexAliasesExecutable.execute(client, container)
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
