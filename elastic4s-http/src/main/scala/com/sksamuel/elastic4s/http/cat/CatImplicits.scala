package com.sksamuel.elastic4s.http.cat

import com.sksamuel.elastic4s.cat._
import com.sksamuel.elastic4s.http.{DefaultResponseHandler, HttpExecutable, ResponseHandler}
import org.elasticsearch.client.{Response, RestClient}

import scala.concurrent.Future
import scala.util.Try

trait CatImplicits {

  implicit object CatShardsExecutable extends HttpExecutable[CatShardsDefinition, Seq[CatShards]] {
    override def execute(client: RestClient, request: CatShardsDefinition): Future[Seq[CatShards]] = {
      client.async("GET", "/_cat/shards?v&format=json&bytes=b", Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatNodesExecutable extends HttpExecutable[CatNodesDefinition, Seq[CatNodes]] {
    override def execute(client: RestClient, request: CatNodesDefinition): Future[Seq[CatNodes]] = {
      client.async("GET", "/_cat/nodes?v&format=json", Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatPluginsExecutable extends HttpExecutable[CatPluginsDefinition, Seq[CatPlugin]] {
    override def execute(client: RestClient, request: CatPluginsDefinition): Future[Seq[CatPlugin]] = {
      client.async("GET", "/_cat/plugins?v&format=json", Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatThreadPoolExecutable extends HttpExecutable[CatThreadPoolDefinition, Seq[CatThreadPool]] {
    override def execute(client: RestClient, request: CatThreadPoolDefinition): Future[Seq[CatThreadPool]] = {
      client.async("GET", "/_cat/thread_pool?v&format=json&h=id,name,active,rejected,completed,type,size,queue,queue_size,largest,min,max,keep_alive,node_id,ephemeral_id,pid,host,ip,port", Map.empty, ResponseHandler.default)
    }
  }

  implicit object CatHealthExecutable extends HttpExecutable[CatHealthDefinition, CatHealth] {
    override def execute(client: RestClient, request: CatHealthDefinition): Future[CatHealth] = {
      client.async("GET", "/_cat/health?v&format=json", Map.empty, new DefaultResponseHandler[CatHealth] {
        override def onResponse(response: Response): Try[CatHealth] = Try {
          ResponseHandler.fromEntity[Seq[CatHealth]](response.getEntity).head
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
        override def onResponse(response: Response): Try[CatCount] = Try {
          ResponseHandler.fromEntity[Seq[CatCount]](response.getEntity).head
        }
      })
    }
  }

  implicit object CatMasterExecutable extends HttpExecutable[CatMasterDefinition, CatMaster] {
    override def execute(client: RestClient, request: CatMasterDefinition): Future[CatMaster] = {
      client.async("GET", "/_cat/master?v&format=json", Map.empty, new DefaultResponseHandler[CatMaster] {
        override def onResponse(response: Response): Try[CatMaster] = Try {
          ResponseHandler.fromEntity[Seq[CatMaster]](response.getEntity).head
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
