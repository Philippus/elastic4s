package com.sksamuel.elastic4s.http.cat

import com.fasterxml.jackson.annotation.JsonProperty

case class CatAlias(alias: String, index: String, filter: String, routing: Routing)
case class Routing(index: String, search: String)

case class CatShards(index: String,
                     shard: String,
                     prirep: String,
                     state: String,
                     docs: Long,
                     store: Long,
                     ip: String,
                     node: String)

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
                      @JsonProperty("docs.count") count: Long,
                      @JsonProperty("docs.deleted") deleted: Long,
                      @JsonProperty("store.size") storeSize: Long)

case class CatAllocation(shards: Int,
                         @JsonProperty("disk.indices") diskIndices: Long,
                         @JsonProperty("disk.used") diskUsed: Long,
                         @JsonProperty("disk.avail") diskAvailable: Long,
                         @JsonProperty("disk.total") diskTotal: Long,
                         @JsonProperty("disk.percent") diskPercent: Double,
                         host: String,
                         ip: String,
                         node: String)

case class CatCount(epoch: Long, timestamp: String, count: Long)

case class CatPlugin(name: String, component: String, version: String)

case class CatNodes(ip: String,
                    @JsonProperty("heap.percent") heapPercent: Double,
                    @JsonProperty("ram.percent") ramPercent: Double,
                    cpu: Double,
                    load_1m: Double,
                    load_5m: Double,
                    load_15m: Double,
                    @JsonProperty("node.role") nodeRole: String,
                    master: String,
                    name: String)

case class CatHealth(epoch: Long,
                     timestamp: String,
                     cluster: String,
                     status: String,
                     @JsonProperty("node.total") nodeTotal: Long,
                     @JsonProperty("node.data") nodeData: Long,
                     shards: Int,
                     pri: Int,
                     relo: Int,
                     init: Int,
                     unassign: Int,
                     pending_tasks: Int,
                     @JsonProperty("max_task_wait_time") maxTaskWaitTime: String,
                     @JsonProperty("active_shards_percent") activeShardsPercent: String
                    )

case class CatThreadPool(id: String,
                         name: String,
                         active: Int,
                         rejected: Int,
                         completed: Int,
                         `type`: String,
                         size: Int,
                         queue: Int,
                         queue_size: Int,
                         largest: Int,
                         min: Int,
                         max: Int,
                         keep_alive: String,
                         node_id: String,
                         pid: Int,
                         host: String,
                         ip: String,
                         port: String)
