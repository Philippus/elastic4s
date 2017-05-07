package com.sksamuel.elastic4s.http.cat

import com.sksamuel.elastic4s.cat._
import com.sksamuel.elastic4s.http.{DefaultResponseHandler, HttpExecutable, ResponseHandler}
import org.elasticsearch.client.{Response, RestClient}

import scala.concurrent.Future
import scala.util.Try

trait CatImplicits {

  implicit object CatHealthExecutable extends HttpExecutable[CatHealthDefinition, CatHealth] {
    override def execute(client: RestClient, request: CatHealthDefinition): Future[CatHealth] = {
      client.async("GET", "/_cat/health?v&format=json", Map.empty, new DefaultResponseHandler[CatHealth] {
        override def onResponse(response: Response): Try[CatHealth] = {
          ResponseHandler.fromEntity[Seq[CatHealth]](response.getEntity).map(_.head)
        }
      })
    }
  }

  implicit object CatCountExecutable extends HttpExecutable[CatCountDefinition, CatCount] {
    override def execute(client: RestClient, request: CatCountDefinition): Future[CatCount] = {
      val endpoint = request.indices match {
        case Nil => "/_cat/count?v&format=json"
        case indexes => "/_cat/count/" + indexes.mkString(",") + "?v&format=json"
      }
      client.async("GET", endpoint, Map.empty, new DefaultResponseHandler[CatCount] {
        override def onResponse(response: Response): Try[CatCount] = {
          ResponseHandler.fromEntity[Seq[CatCount]](response.getEntity).map(_.head)
        }
      })
    }
  }

  implicit object CatMasterExecutable extends HttpExecutable[CatMasterDefinition, CatMaster] {
    override def execute(client: RestClient, request: CatMasterDefinition): Future[CatMaster] = {
      client.async("GET", "/_cat/master?v&format=json", Map.empty, new DefaultResponseHandler[CatMaster] {
        override def onResponse(response: Response): Try[CatMaster] = {
          ResponseHandler.fromEntity[Seq[CatMaster]](response.getEntity).map(_.head)
        }
      })
    }
  }

  implicit object CatAliasesExecutable extends HttpExecutable[CatAliasesDefinition, Seq[CatAlias]] {
    override def execute(client: RestClient, request: CatAliasesDefinition): Future[Seq[CatAlias]] = {
      client.async("GET", "/_cat/aliases?v&format=json", Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatIndexesExecutable extends HttpExecutable[CatIndexesDefinition, Seq[CatIndices]] {

    val BaseEndpoint = "/_cat/indices?v&format=json&bytes=b"

    override def execute(client: RestClient, request: CatIndexesDefinition): Future[Seq[CatIndices]] = {
      val endpoint = request.health match {
        case Some(health) => BaseEndpoint + "&health=" + health.getClass.getSimpleName.toLowerCase.stripSuffix("$")
        case _ => BaseEndpoint
      }
      client.async("GET", endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatAllocationExecutable extends HttpExecutable[CatAllocationDefinition, Seq[CatAllocation]] {

    override def execute(client: RestClient, request: CatAllocationDefinition): Future[Seq[CatAllocation]] = {
      client.async("GET", "/_cat/aliases?v&format=json&bytes=b", Map.empty, ResponseHandler.default)
    }
  }
}
