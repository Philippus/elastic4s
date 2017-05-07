package com.sksamuel.elastic4s.http.cat

import com.sksamuel.elastic4s.cat.{CatAliasesDefinition, CatAllocationDefinition, CatIndexesDefinition, CatMasterDefinition}
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

trait CatImplicits {

  implicit object CatMasterExecutable extends HttpExecutable[CatMasterDefinition, Seq[CatMaster]] {

    override def execute(client: RestClient, request: CatMasterDefinition): Future[Seq[CatMaster]] = {
      client.async("GET", "/_cat/master?v&format=json", Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatAliasesExecutable extends HttpExecutable[CatAliasesDefinition, Seq[CatAlias]] {

    override def execute(client: RestClient, request: CatAliasesDefinition): Future[Seq[CatAlias]] = {
      client.async("GET", "/_cat/aliases?v&format=json", Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatIndexesExecutable extends HttpExecutable[CatIndexesDefinition, Seq[CatIndices]] {

    val BaseEndpoint = "/_cat/indices?v&format=json"

    override def execute(client: RestClient, request: CatIndexesDefinition): Future[Seq[CatIndices]] = {
      val endpoint = request.health match {
        case Some(health) => BaseEndpoint + "&health=" + health.getClass.getSimpleName.toLowerCase.stripSuffix("$")
        case _ => BaseEndpoint
      }
      client.async("GET", endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatAllocationExecutable extends HttpExecutable[CatAllocationDefinition, CatAllocation] {

    override def execute(client: RestClient, request: CatAllocationDefinition): Future[CatAllocation] = {
      client.async("GET", "/_cat/aliases?v&format=json&bytes=b", Map.empty, ResponseHandler.default)
    }
  }
}

case class CatAlias(alias: String, index: String, filter: String, routing: Routing)
case class Routing(index: String, search: String)

case class CatMaster(id: String,
                     host: String,
                     ip: String,
                     node: String)

case class CatIndices(health: String,
                      status: String,
                      index: String,
                      uuid: String,
                      pri: Int,
                      rep: Int,
                      docs: Docs,
                      store: Store)
case class Store(size: String)
case class Docs(count: Long, deleted: Long)

case class CatAllocation(shards: Int,
                         disk: Disk,
                         host: String,
                         ip: String,
                         node: String)

case class Disk(indices: Long,
                used: Long,
                avail: Long,
                total: Long,
                percent: Double)
