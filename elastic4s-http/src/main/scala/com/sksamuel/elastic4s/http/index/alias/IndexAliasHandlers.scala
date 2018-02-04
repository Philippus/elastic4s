package com.sksamuel.elastic4s.http.index.alias

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.alias.{AddAliasActionRequest, GetAliasesRequest, IndicesAliasesRequest, RemoveAliasAction}
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.http.index.admin.AliasActionResponse
import org.apache.http.entity.ContentType

trait IndexAliasHandlers {

  implicit object GetAliasHandler extends Handler[GetAliasesRequest, IndexAliases] {

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
        case _   => Left(ElasticError.parse(response))
      }
    }

    override def requestHandler(request: GetAliasesRequest): ElasticRequest = {
      val endpoint = s"/${request.indices.string}/_alias/${request.aliases.mkString(",")}"
      val params = request.ignoreUnavailable.fold(Map.empty[String, Any]) { ignore =>
        Map("ignore_unavailable" -> ignore)
      }
      ElasticRequest("GET", endpoint, params)
    }
  }

  implicit object RemoveAliasActionHandler extends Handler[RemoveAliasAction, AliasActionResponse] {
    override def requestHandler(request: RemoveAliasAction): ElasticRequest = {
      val container = IndicesAliasesRequest(Seq(request))
      IndexAliasesHandler.requestHandler(container)
    }
  }

  implicit object AddAliasActionHandler extends Handler[AddAliasActionRequest, AliasActionResponse] {
    override def requestHandler(request: AddAliasActionRequest): ElasticRequest = {
      val container = IndicesAliasesRequest(Seq(request))
      IndexAliasesHandler.requestHandler(container)
    }
  }

  implicit object IndexAliasesHandler extends Handler[IndicesAliasesRequest, AliasActionResponse] {
    override def requestHandler(request: IndicesAliasesRequest): ElasticRequest = {
      val body   = AliasActionBuilder(request).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)
      ElasticRequest("POST", "/_aliases", entity)
    }
  }
}

case class IndexAliases(mappings: Map[Index, Seq[Alias]]) {
  def aliasesForIndex(index: String): Seq[Alias] = aliasesForIndex(Index(index))
  def aliasesForIndex(index: Index): Seq[Alias]  = mappings.getOrElse(index, Nil)
}

case class Alias(name: String)
