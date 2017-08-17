package com.sksamuel.elastic4s.http.index.alias

import com.sksamuel.elastic4s.alias.{AddAliasActionDefinition, GetAliasDefinition, GetAliasesDefinition, IndicesAliasesRequestDefinition, RemoveAliasActionDefinition}
import com.sksamuel.elastic4s.http.index.admin.IndicesAliasResponse
import com.sksamuel.elastic4s.http.{HttpEntity, HttpExecutable, HttpRequestClient, HttpResponse, ResponseHandler}
import org.elasticsearch.client.http.entity.ContentType

import scala.concurrent.Future
import scala.util.Try

trait IndexAliasImplicits {

  implicit object GetAliasesDefinition extends HttpExecutable[GetAliasesDefinition, Seq[Alias]] {

    override def responseHandler: ResponseHandler[Seq[Alias]] = new ResponseHandler[Seq[Alias]] {
      override def handle(response: HttpResponse): Try[Seq[Alias]] = Try {
        ResponseHandler.fromEntity[Map[String, Map[String, Map[String, AnyRef]]]](response.entity.get).map { case (name, map) =>
          Alias(name, map.getOrElse("aliases", Map.empty).keySet.toSeq)
        }.toSeq
      }
    }

    override def execute(client: HttpRequestClient, request: GetAliasesDefinition): Future[HttpResponse] = {
      client.async("GET", "/_aliases", Map.empty)
    }
  }

  implicit object GetAliasExecutable extends HttpExecutable[GetAliasDefinition, Option[Alias]] {

    override def responseHandler: ResponseHandler[Option[Alias]] = new ResponseHandler[Option[Alias]] {

      import scala.collection.JavaConverters._

      override def handle(response: HttpResponse): Try[Option[Alias]] = {
        response.statusCode match {
          case 200 => Try {
            val root = ResponseHandler.json(response.entity.get)
            root.fields.asScala.toVector.headOption.map { entry =>
              val aliases = entry.getValue.findValue("aliases").fieldNames().asScala.toList
              Alias(entry.getKey, aliases)
            }
          }
          case 404 => Try(None)
          case _ => super.handleError(response)
        }
      }
    }

    override def execute(client: HttpRequestClient, request: GetAliasDefinition): Future[HttpResponse] = {
      val indexPathElement = if (request.indices.isEmpty) "" else s"/${request.indices.mkString(",")}"
      val endpoint = s"$indexPathElement/_alias/${request.aliases.mkString(",")}"
      val params = request.ignoreUnavailable.fold(Map.empty[String, Any]) { ignore => Map("ignore_unavailable" -> ignore) }
      client.async("GET", endpoint, params)
    }
  }

  implicit object RemoveAliasActionExecutable extends HttpExecutable[RemoveAliasActionDefinition, IndicesAliasResponse] {
    override def execute(client: HttpRequestClient, request: RemoveAliasActionDefinition): Future[HttpResponse] = {
      val container = IndicesAliasesRequestDefinition(Seq(request))
      IndexAliasesExecutable.execute(client, container)
    }
  }

  implicit object AddAliasActionExecutable extends HttpExecutable[AddAliasActionDefinition, IndicesAliasResponse] {
    override def execute(client: HttpRequestClient, request: AddAliasActionDefinition): Future[HttpResponse] = {
      val container = IndicesAliasesRequestDefinition(Seq(request))
      IndexAliasesExecutable.execute(client, container)
    }
  }

  implicit object IndexAliasesExecutable extends HttpExecutable[IndicesAliasesRequestDefinition, IndicesAliasResponse] {
    override def execute(client: HttpRequestClient, request: IndicesAliasesRequestDefinition): Future[HttpResponse] = {
      val body = AliasActionBuilder(request).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)
      client.async("POST", "/_aliases", Map.empty, entity)
    }
  }
}

case class Alias(name: String, indexes: Seq[String])
