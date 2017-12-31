package com.sksamuel.elastic4s.http.cat

import cats.Functor
import com.sksamuel.elastic4s.cat._
import com.sksamuel.elastic4s.http._

trait CatImplicits {

  implicit object CatSegmentsExecutable extends HttpExecutable[CatSegments, Seq[CatSegmentsResponse]] {
    override def execute[F[_]: FromListener ](client: HttpRequestClient, request: CatSegments): F[HttpResponse] = {
      val endpoint = if (request.indices.isAll) "/_cat/segments" else "/_cat/segments/" + request.indices.string
      client.async("GET", s"$endpoint?v&format=json&bytes=b", Map.empty)
    }
  }

  implicit object CatShardsExecutable extends HttpExecutable[CatShards, Seq[CatShardsResponse]] {
    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatShards): F[HttpResponse] = {
      client.async("GET", "/_cat/shards?v&format=json&bytes=b", Map.empty)
    }
  }

  implicit object CatNodesExecutable extends HttpExecutable[CatNodes, Seq[CatNodesResponse]] {
    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatNodes): F[HttpResponse] = {
      val headers = Seq(
        "id", "pid", "ip", "port", "http_address", "version", "build", "jdk", "disk.avail", "heap.current", "heap.percent", "heap.max", "ram.current", "ram.percent", "ram.max", "file_desc.current", "file_desc.percent", "file_desc.max", "cpu", "load_1m", "load_5m", "load_15m", "uptime", "node.role", "master", "name", "completion.size", "fielddata.memory_size", "fielddata.evictions", "query_cache.memory_size", "query_cache.evictions", "request_cache.memory_size", "request_cache.evictions", "request_cache.miss_count", "flush.total"
      ).mkString(",")
      client.async("GET", s"/_cat/nodes?v&h=$headers&format=json", Map.empty)
    }
  }

  implicit object CatPluginsExecutable extends HttpExecutable[CatPlugins, Seq[CatPluginResponse]] {
    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatPlugins): F[HttpResponse] = {
      client.async("GET", "/_cat/plugins?v&format=json", Map.empty)
    }
  }

  implicit object CatThreadPoolExecutable extends HttpExecutable[CatThreadPool, Seq[CatThreadPoolResponse]] {
    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatThreadPool): F[HttpResponse] = {
      val headers = "id,name,active,rejected,completed,type,size,queue,queue_size,largest,min,max,keep_alive,node_id,ephemeral_id,pid,host,ip,port"
      client.async("GET", s"/_cat/thread_pool?v&format=json&h=$headers", Map.empty)
    }
  }

  implicit object CatHealthExecutable extends HttpExecutable[CatHealth, CatHealthResponse] {

    override def responseHandler: ResponseHandler[CatHealthResponse] = new ResponseHandler[CatHealthResponse] {
      override def handle(response: HttpResponse) = Right(ResponseHandler.fromResponse[Seq[CatHealthResponse]](response).head)
    }

    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatHealth): F[HttpResponse] = {
      client.async("GET", "/_cat/health?v&format=json", Map.empty)
    }
  }

  implicit object CatCountExecutable extends HttpExecutable[CatCount, CatCountResponse] {

    override def responseHandler: ResponseHandler[CatCountResponse] = new ResponseHandler[CatCountResponse] {
      override def handle(response: HttpResponse) = Right(ResponseHandler.fromResponse[Seq[CatCountResponse]](response).head)
    }

    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatCount): F[HttpResponse] = {
      val endpoint = request.indices match {
        case Nil => "/_cat/count?v&format=json"
        case indexes => "/_cat/count/" + indexes.mkString(",") + "?v&format=json"
      }
      client.async("GET", endpoint, Map.empty)
    }
  }

  implicit object CatMasterExecutable extends HttpExecutable[CatMaster, CatMasterResponse] {

    override def responseHandler: ResponseHandler[CatMasterResponse] = new ResponseHandler[CatMasterResponse] {
      override def handle(response: HttpResponse) = Right(ResponseHandler.fromResponse[Seq[CatMasterResponse]](response).head)
    }

    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatMaster): F[HttpResponse] = {
      client.async("GET", "/_cat/master?v&format=json", Map.empty)
    }
  }

  implicit object CatAliasesExecutable extends HttpExecutable[CatAliases, Seq[CatAliasResponse]] {
    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatAliases): F[HttpResponse] = {
      client.async("GET", "/_cat/aliases?v&format=json", Map.empty)
    }
  }

  implicit object CatIndexesExecutable extends HttpExecutable[CatIndexes, Seq[CatIndicesResponse]] {

    val BaseEndpoint = "/_cat/indices?v&format=json&bytes=b"

    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatIndexes): F[HttpResponse] = {
      val endpoint = request.health match {
        case Some(health) => BaseEndpoint + "&health=" + health.getClass.getSimpleName.toLowerCase.stripSuffix("$")
        case _ => BaseEndpoint
      }
      client.async("GET", endpoint, Map.empty)
    }
  }

  implicit object CatAllocationExecutable extends HttpExecutable[CatAllocation, Seq[CatAllocationResponse]] {

    override def execute[F[_]: FromListener](client: HttpRequestClient, request: CatAllocation): F[HttpResponse] = {
      client.async("GET", "/_cat/aliases?v&format=json&bytes=b", Map.empty)
    }
  }
}
