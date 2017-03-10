package com.sksamuel.elastic4s.http.cluster

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.cluster.ClusterStateDefinition
import com.sksamuel.elastic4s.http.HttpExecutable
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait ClusterImplicits {

  implicit object ClusterStateHttpExecutable extends HttpExecutable[ClusterStateDefinition, ClusterStateResponse] {
    val method = "GET"

    override def execute(client: RestClient,
                         request: ClusterStateDefinition,
                         format: JsonFormat[ClusterStateResponse]): Future[ClusterStateResponse] = {
      val endpoint = "/_cluster/state" + buildMetricsString(request.metrics) + buildIndexString(request.indices)
      logger.debug(s"Accessing endpoint $endpoint")
      executeAsyncAndMapResponse(client.performRequestAsync(method, endpoint, Map.empty[String, String].asJava, _: ResponseListener), format)
    }

    private def buildMetricsString(metrics: Seq[String]): String = {
      if (metrics.isEmpty) {
        "/_all"
      } else {
        "/" + metrics.mkString(",")
      }
    }

    private def buildIndexString(indices: Seq[String]): String = {
      if (indices.isEmpty) {
        ""
      } else {
        "/" + indices.mkString(",")
      }
    }
  }

}

object ClusterStateResponse {
  case class Index(state: String, aliases: Seq[String])
  case class Metadata(cluster_uuid: String, indices: Map[String, Index])
}
case class ClusterStateResponse(cluster_name: String, master_node: String, metadata: Option[ClusterStateResponse.Metadata])
