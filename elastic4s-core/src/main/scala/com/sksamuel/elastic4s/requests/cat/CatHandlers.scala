package com.sksamuel.elastic4s.requests.cat

import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpResponse, ResponseHandler}

trait CatHandlers {

  implicit object CatSegmentsHandler extends Handler[CatSegments, Seq[CatSegmentsResponse]] {
    override def build(request: CatSegments): ElasticRequest = {
      val endpoint = if (request.indices.isAll) "/_cat/segments" else "/_cat/segments/" + request.indices.string
      ElasticRequest("GET", endpoint, Map("v" -> "", "format" -> "json", "bytes" -> "b"))
    }
  }

  implicit object CatShardsHandler extends Handler[CatShards, Seq[CatShardsResponse]] {
    override def build(request: CatShards): ElasticRequest =
      ElasticRequest("GET", "/_cat/shards", Map("v" -> "", "format" -> "json", "bytes" -> "b"))
  }

  implicit object CatNodesHandler extends Handler[CatNodes, Seq[CatNodesResponse]] {
    override def build(request: CatNodes): ElasticRequest = {
      val headers: String = Seq(
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
      ElasticRequest("GET", "/_cat/nodes", Map("v" -> "", "format" -> "json", "h" -> headers))
    }
  }

  implicit object CatPluginsHandler extends Handler[CatPlugins, Seq[CatPluginResponse]] {
    override def build(request: CatPlugins): ElasticRequest =
      ElasticRequest("GET", "/_cat/plugins", Map("v" -> "", "format" -> "json"))
  }

  implicit object CatThreadPoolHandler extends Handler[CatThreadPool, Seq[CatThreadPoolResponse]] {
    override def build(request: CatThreadPool): ElasticRequest = {
      val headers: String =
        "id,name,active,rejected,completed,type,size,queue,queue_size,largest,min,max,keep_alive,node_id,ephemeral_id,pid,host,ip,port"
      ElasticRequest("GET", s"/_cat/thread_pool", Map("v" -> "", "format" -> "json", "h" -> headers))
    }
  }

  implicit object CatHealthHandler extends Handler[CatHealth, CatHealthResponse] {

    override def responseHandler: ResponseHandler[CatHealthResponse] = new ResponseHandler[CatHealthResponse] {
      override def handle(response: HttpResponse) =
        Right(ResponseHandler.fromResponse[Seq[CatHealthResponse]](response).head)
    }

    override def build(request: CatHealth): ElasticRequest =
      ElasticRequest("GET", "/_cat/health", Map("v" -> "", "format" -> "json"))
  }

  implicit object CatCountHandler extends Handler[CatCount, CatCountResponse] {

    override def responseHandler: ResponseHandler[CatCountResponse] = new ResponseHandler[CatCountResponse] {
      override def handle(response: HttpResponse) =
        Right(ResponseHandler.fromResponse[Seq[CatCountResponse]](response).head)
    }

    override def build(request: CatCount): ElasticRequest = {
      val endpoint = request.indices match {
        case Nil => "/_cat/count"
        case indexes => "/_cat/count/" + indexes.mkString(",")
      }
      ElasticRequest("GET", endpoint, Map("v" -> "", "format" -> "json"))
    }
  }

  implicit object CatMasterHandler extends Handler[CatMaster, CatMasterResponse] {

    override def responseHandler: ResponseHandler[CatMasterResponse] = new ResponseHandler[CatMasterResponse] {
      override def handle(response: HttpResponse) =
        Right(ResponseHandler.fromResponse[Seq[CatMasterResponse]](response).head)
    }

    override def build(request: CatMaster): ElasticRequest =
      ElasticRequest("GET", "/_cat/master", Map("v" -> "", "format" -> "json"))
  }

  implicit object CatAliasesHandler extends Handler[CatAliases, Seq[CatAliasResponse]] {
    override def build(request: CatAliases): ElasticRequest =
      ElasticRequest("GET", "/_cat/aliases", Map("v" -> "", "format" -> "json"))
  }

  implicit object CatIndexesHandler extends Handler[CatIndexes, Seq[CatIndicesResponse]] {

    val BaseParams: Map[String, Any] = Map("v" -> "", "format" -> "json", "bytes" -> "b")

    override def build(request: CatIndexes): ElasticRequest = {
      val params = request.health match {
        case Some(health) => BaseParams + ("health" -> health.getClass.getSimpleName.toLowerCase.stripSuffix("$"))
        case _ => BaseParams
      }
      ElasticRequest("GET", "/_cat/indices", params)
    }
  }

  implicit object CatAllocationHandler extends Handler[CatAllocation, Seq[CatAllocationResponse]] {

    override def build(request: CatAllocation): ElasticRequest =
      ElasticRequest("GET", "/_cat/allocation", Map("v" -> "", "format" -> "json", "bytes" -> "b"))
  }

}
