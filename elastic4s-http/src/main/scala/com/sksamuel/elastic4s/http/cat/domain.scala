package com.sksamuel.elastic4s.http.cat


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
case class Store(size: Long)
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

case class CatCount(epoch: Long, timestamp: String, count: Long)

case class CatPlugin(name: String, component: String, version: String)

case class CatShard()

case class Node(total: Long,
                data: Long)

case class CatHealth(epoch: Long,
                     timestamp: String,
                     cluster: String,
                     status: String,
                     node: Node,
                     shards: Int,
                     pri: Int,
                     relo: Int,
                     init: Int,
                     unassign: Int,
                     pending_tasks: Int,
                     private val max_task_wait_time: String,
                     private val active_shards_percent: String
                    ) {
  def maxTaskWaitTime: String = max_task_wait_time
  def activeShardsPercent: String = active_shards_percent
}

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
