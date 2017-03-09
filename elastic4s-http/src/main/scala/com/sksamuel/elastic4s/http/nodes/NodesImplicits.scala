package com.sksamuel.elastic4s.http.nodes

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.nodes.NodeStatsDefinition
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class SwapStats(total_in_bytes: Long, free_in_bytes: Long, used_in_bytes: Long) {
  def totalInBytes: Long = total_in_bytes
  def freeInBytes: Long = free_in_bytes
  def usedInBytes: Long = used_in_bytes
}
case class MemoryStats(total_in_bytes: Long, free_in_bytes: Long, used_in_bytes: Long, free_percent: Int, used_percent: Int) {
  def totalInBytes: Long = total_in_bytes
  def freeInBytes: Long = free_in_bytes
  def usedInBytes: Long = used_in_bytes
  def freePercent: Int = free_percent
  def usedPercent: Int = used_percent
}
case class OsStats(cpu_percent: Int, load_average: Double, mem: MemoryStats, swap: SwapStats){
  def cpuPercent: Int = cpu_percent
  def loadAverage: Double = load_average
}
case class NodeStats(name: String, transport_address: String, host: String, ip: Seq[String], os: Option[OsStats]) {
  def transportAddress: String = transport_address
}
case class NodesStatsResponse(cluster_name: String, nodes: Map[String, NodeStats]) {
  def clusterName: String = cluster_name
}

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
