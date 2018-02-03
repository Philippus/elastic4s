package com.sksamuel.elastic4s.http.cat

import com.sksamuel.elastic4s.cat._
import com.sksamuel.elastic4s.http._

trait CatHandlers {

  implicit object CatSegmentsHandler extends Handler[CatSegments, Seq[CatSegmentsResponse]] {
    override def requestHandler(request: CatSegments): ElasticRequest = {
      val endpoint = if (request.indices.isAll) "/_cat/segments" else "/_cat/segments/" + request.indices.string
      ElasticRequest("GET", s"$endpoint?v&format=json&bytes=b")
    }
  }

  implicit object CatShardsHandler extends Handler[CatShards, Seq[CatShardsResponse]] {
    override def requestHandler(request: CatShards): ElasticRequest =
      ElasticRequest("GET", "/_cat/shards?v&format=json&bytes=b")
  }

  implicit object CatNodesHandler extends Handler[CatNodes, Seq[CatNodesResponse]] {
    override def requestHandler(request: CatNodes): ElasticRequest = {
      val headers = Seq(
        "id",
        "pid",
        "ip",
        "port",
        "http_address",
        "version",
        "build",
        "jdk",
        "disk.avail",
        "heap.current",
        "heap.percent",
        "heap.max",
        "ram.current",
        "ram.percent",
        "ram.max",
        "file_desc.current",
        "file_desc.percent",
        "file_desc.max",
        "cpu",
        "load_1m",
        "load_5m",
        "load_15m",
        "uptime",
        "node.role",
        "master",
        "name",
        "completion.size",
        "fielddata.memory_size",
        "fielddata.evictions",
        "query_cache.memory_size",
        "query_cache.evictions",
        "request_cache.memory_size",
        "request_cache.evictions",
        "request_cache.miss_count",
        "flush.total"
      ).mkString(",")
      ElasticRequest("GET", s"/_cat/nodes?v&h=$headers&format=json")
    }
  }

  implicit object CatPluginsHandler extends Handler[CatPlugins, Seq[CatPluginResponse]] {
    override def requestHandler(request: CatPlugins): ElasticRequest =
      ElasticRequest("GET", "/_cat/plugins?v&format=json")
  }

  implicit object CatThreadPoolHandler extends Handler[CatThreadPool, Seq[CatThreadPoolResponse]] {
    override def requestHandler(request: CatThreadPool): ElasticRequest = {
      val headers =
        "id,name,active,rejected,completed,type,size,queue,queue_size,largest,min,max,keep_alive,node_id,ephemeral_id,pid,host,ip,port"
      ElasticRequest("GET", s"/_cat/thread_pool?v&format=json&h=$headers")
    }
  }

  implicit object CatHealthHandler extends Handler[CatHealth, CatHealthResponse] {

    override def responseHandler: ResponseHandler[CatHealthResponse] = new ResponseHandler[CatHealthResponse] {
      override def handle(response: HttpResponse) =
        Right(ResponseHandler.fromResponse[Seq[CatHealthResponse]](response).head)
    }

    override def requestHandler(request: CatHealth): ElasticRequest =
      ElasticRequest("GET", "/_cat/health?v&format=json")
  }

  implicit object CatCountHandler extends Handler[CatCount, CatCountResponse] {

    override def responseHandler: ResponseHandler[CatCountResponse] = new ResponseHandler[CatCountResponse] {
      override def handle(response: HttpResponse) =
        Right(ResponseHandler.fromResponse[Seq[CatCountResponse]](response).head)
    }

    override def requestHandler(request: CatCount): ElasticRequest = {
      val endpoint = request.indices match {
        case Nil => "/_cat/count?v&format=json"
        case indexes => "/_cat/count/" + indexes.mkString(",") + "?v&format=json"
      }
      ElasticRequest("GET", endpoint)
    }
  }

  implicit object CatMasterHandler extends Handler[CatMaster, CatMasterResponse] {

    override def responseHandler: ResponseHandler[CatMasterResponse] = new ResponseHandler[CatMasterResponse] {
      override def handle(response: HttpResponse) =
        Right(ResponseHandler.fromResponse[Seq[CatMasterResponse]](response).head)
    }

    override def requestHandler(request: CatMaster): ElasticRequest =
      ElasticRequest("GET", "/_cat/master?v&format=json")
  }

  implicit object CatAliasesHandler extends Handler[CatAliases, Seq[CatAliasResponse]] {
    override def requestHandler(request: CatAliases): ElasticRequest =
      ElasticRequest("GET", "/_cat/aliases?v&format=json")
  }

  implicit object CatIndexesHandler extends Handler[CatIndexes, Seq[CatIndicesResponse]] {

    val BaseEndpoint = "/_cat/indices?v&format=json&bytes=b"

    override def requestHandler(request: CatIndexes): ElasticRequest = {
      val endpoint = request.health match {
        case Some(health) => BaseEndpoint + "&health=" + health.getClass.getSimpleName.toLowerCase.stripSuffix("$")
        case _ => BaseEndpoint
      }
      ElasticRequest("GET", endpoint)
    }
  }

  implicit object CatAllocationHandler extends Handler[CatAllocation, Seq[CatAllocationResponse]] {

    override def requestHandler(request: CatAllocation): ElasticRequest =
      ElasticRequest("GET", "/_cat/aliases?v&format=json&bytes=b")
  }
}
