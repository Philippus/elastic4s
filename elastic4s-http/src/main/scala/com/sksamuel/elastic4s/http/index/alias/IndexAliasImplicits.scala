package com.sksamuel.elastic4s.http.index.alias

import cats.Functor
import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.alias.{AddAliasActionDefinition, GetAliasesDefinition, IndicesAliasesRequestDefinition, RemoveAliasActionDefinition}
import com.sksamuel.elastic4s.http.index.admin.AliasActionResponse
import com.sksamuel.elastic4s.http._
import org.apache.http.entity.ContentType

trait IndexAliasImplicits {

  implicit object GetAliasHttpExecutable extends HttpExecutable[GetAliasesDefinition, IndexAliases] {

    import scala.collection.JavaConverters._

    override def responseHandler = new ResponseHandler[IndexAliases] {
      override def handle(response: HttpResponse): Either[ElasticError, IndexAliases] = response.statusCode match {
        case 200 =>
          val root = ResponseHandler.json(response.entity.get)
          val map = root.fields.asScala.toVector.map { entry =>
            Index(entry.getKey) -> entry.getValue.get("aliases").fieldNames.asScala.toList.map(Alias.apply)
          }.toMap
          Right(IndexAliases(map))
        case 404 => Right(IndexAliases(Map.empty))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: GetAliasesDefinition): F[HttpResponse] = {
      val endpoint = s"/${request.indices.string}/_alias/${request.aliases.mkString(",")}"
      val params = request.ignoreUnavailable.fold(Map.empty[String, Any]) { ignore => Map("ignore_unavailable" -> ignore) }
      client.async("GET", endpoint, params)
    }
  }

  implicit object RemoveAliasActionExecutable extends HttpExecutable[RemoveAliasActionDefinition, AliasActionResponse] {
    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: RemoveAliasActionDefinition): F[HttpResponse] = {
      val container = IndicesAliasesRequestDefinition(Seq(request))
      IndexAliasesExecutable.execute[F](client, container)
    }
  }

  implicit object AddAliasActionExecutable extends HttpExecutable[AddAliasActionDefinition, AliasActionResponse] {
    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: AddAliasActionDefinition): F[HttpResponse] = {
      val container = IndicesAliasesRequestDefinition(Seq(request))
      IndexAliasesExecutable.execute[F](client, container)
    }
  }

  implicit object IndexAliasesExecutable extends HttpExecutable[IndicesAliasesRequestDefinition, AliasActionResponse] {
    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: IndicesAliasesRequestDefinition): F[HttpResponse] = {
      val body = AliasActionBuilder(request).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)
      client.async("POST", "/_aliases", Map.empty, entity)
    }
  }
}

case class IndexAliases(mappings: Map[Index, Seq[Alias]]) {
  def aliasesForIndex(index: String): Seq[Alias] = aliasesForIndex(Index(index))
  def aliasesForIndex(index: Index): Seq[Alias] = mappings.getOrElse(index, Nil)
}

case class Alias(name: String)
