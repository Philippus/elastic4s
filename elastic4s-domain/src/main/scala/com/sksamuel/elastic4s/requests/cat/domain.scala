package com.sksamuel.elastic4s.requests.cat

import com.fasterxml.jackson.annotation.JsonProperty

case class CatAliasResponse(alias: String, index: String, filter: String, routing: Routing)
case class Routing(index: String, search: String)

case class CatSegmentsResponse(
  index: String,
  shard: String,
  prirep: String,
  ip: String,
  segment: String,
  generation: String,
  @JsonProperty("docs.count") docsCount: Long,
  @JsonProperty("docs.deleted") docsDeleted: Long,
  size: Long,
  @JsonProperty("size.memory") sizeMemory: Long,
  committed: Boolean,
  searchable: Boolean,
  version: String,
  compound: Boolean
)

case class CatShardsResponse(index: String,
                             shard: String,
                             prirep: String,
                             state: String,
                             docs: Long,
                             store: Long,
                             ip: String,
                             node: String)

case class CatMasterResponse(id: String, host: String, ip: String, node: String)

case class CatIndicesResponse(health: String,
                              status: String,
                              index: String,
                              uuid: String,
                              pri: Int,
                              rep: Int,
                              @JsonProperty("docs.count") count: Long,
                              @JsonProperty("docs.deleted") deleted: Long,
                              @JsonProperty("store.size") storeSize: Long,
                              @JsonProperty("pri.store.size") priStoreSize: Long)

case class CatAllocationResponse(shards: Int,
                                 @JsonProperty("disk.indices") diskIndices: Long,
                                 @JsonProperty("disk.used") diskUsed: Long,
                                 @JsonProperty("disk.avail") diskAvailable: Long,
                                 @JsonProperty("disk.total") diskTotal: Long,
                                 @JsonProperty("disk.percent") diskPercent: Double,
                                 host: String,
                                 ip: String,
                                 node: String)

case class CatCountResponse(epoch: Long, timestamp: String, count: Long)

case class CatPluginResponse(name: String, component: String, version: String)

case class CatNodesResponse(id: String,
                            ip: String,
                            pid: String,
                            cpu: Double,
                            uptime: String,
                            @JsonProperty("heap.percent") heapPercent: Double,
                            @JsonProperty("ram.percent") ramPercent: Double,
                            port: Int,
                            load_1m: Double,
                            load_5m: Double,
                            load_15m: Double,
                            @JsonProperty("node.role") nodeRole: String,
                            master: String,
                            name: String)

case class CatHealthResponse(epoch: Long,
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
                             @JsonProperty("pending_tasks") pendingTasks: Int,
                             @JsonProperty("max_task_wait_time") maxTaskWaitTime: String,
                             @JsonProperty("active_shards_percent") activeShardsPercent: String)

case class CatThreadPoolResponse(id: String,
                                 name: String,
                                 active: Int,
                                 rejected: Int,
                                 completed: Int,
                                 `type`: String,
                                 size: Int,
                                 queue: Int,
                                 @JsonProperty("queue_size") queueSize: Int,
                                 largest: Int,
                                 min: Int,
                                 max: Int,
                                 @JsonProperty("keep_alive") keepAlive: String,
                                 node_id: String,
                                 pid: Int,
                                 host: String,
                                 ip: String,
                                 port: String)
