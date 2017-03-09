package com.sksamuel.elastic4s.http.nodes

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.nodes.NodeStatsDefinition
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class SwapStats(totalInBytes: Long, freeInBytes: Long, usedInBytes: Long)
case class MemoryStats(totalInBytes: Long, freeInBytes: Long, usedInBytes: Long, freePercent: Int, usedPercent: Int)
case class OsStats(cpuPercent: Int, loadAverage: Double, mem: MemoryStats, swap: SwapStats)
case class NodeStats(name: String, transportAddress: String, host: String, ip: Seq[String], os: OsStats)
case class NodesStatsResponse(clusterName: String, nodes: Map[String, NodeStats])

trait NodesImplicits {

  implicit object NodeStatsExecutable extends HttpExecutable[NodeStatsDefinition, NodesStatsResponse] {
    override def execute(client: RestClient,
                         request: NodeStatsDefinition,
                         format: JsonFormat[NodesStatsResponse]): Future[NodesStatsResponse] = {
      val method = "GET"
      val endpoint = buildUrlFromDefinition(request)
      logger.debug(s"Accesing endpoint $endpoint")
      executeAsyncAndMapResponse(client.performRequestAsync(method, endpoint, Map.empty[String, String].asJava, _: ResponseListener), format)
    }

    private def buildUrlFromDefinition(definition: NodeStatsDefinition): String = {
      val baseUrl = "/_nodes"

      if (definition.nodes.nonEmpty) {
        baseUrl + "/" + definition.nodes.mkString(",") + "/stats/" + definition.stats.mkString(",")
      } else {
        baseUrl + "/stats/" + definition.stats.mkString(",")
      }
    }
  }


}
