package com.sksamuel.elastic4s.requests.indexes.alias

import com.sksamuel.elastic4s.requests.alias.{AddAliasActionRequest, GetAliasesRequest, IndicesAliasesRequest, RemoveAliasAction}
import com.sksamuel.elastic4s.requests.indexes.admin.AliasActionResponse
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, Index, ResponseHandler}

import scala.collection.JavaConverters._

trait IndexAliasHandlers {

  implicit object GetAliasHandler extends Handler[GetAliasesRequest, IndexAliases] {

    override def responseHandler: ResponseHandler[IndexAliases] = new ResponseHandler[IndexAliases] {
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

    override def build(request: GetAliasesRequest): ElasticRequest = {
      val endpoint = s"/${request.indices.string}/_alias/${request.aliases.mkString(",")}"
      val params = request.ignoreUnavailable.fold(Map.empty[String, Any]) { ignore =>
        Map("ignore_unavailable" -> ignore)
      }
      ElasticRequest("GET", endpoint, params)
    }
  }

  implicit object RemoveAliasActionHandler extends Handler[RemoveAliasAction, AliasActionResponse] {
    override def build(request: RemoveAliasAction): ElasticRequest = {
      val container = IndicesAliasesRequest(Seq(request))
      IndexAliasesHandler.build(container)
    }
  }

  implicit object AddAliasActionHandler extends Handler[AddAliasActionRequest, AliasActionResponse] {
    override def build(request: AddAliasActionRequest): ElasticRequest = {
      val container = IndicesAliasesRequest(Seq(request))
      IndexAliasesHandler.build(container)
    }
  }

  implicit object IndexAliasesHandler extends Handler[IndicesAliasesRequest, AliasActionResponse] {
    override def build(request: IndicesAliasesRequest): ElasticRequest = {
      val body   = AliasActionBuilder(request).string()
      val entity = HttpEntity(body, "application/json")
      ElasticRequest("POST", "/_aliases", entity)
    }
  }
}

case class IndexAliases(mappings: Map[Index, Seq[Alias]]) {
  def aliasesForIndex(index: String): Seq[Alias] = aliasesForIndex(Index(index))
  def aliasesForIndex(index: Index): Seq[Alias]  = mappings.getOrElse(index, Nil)
}

case class Alias(name: String)
