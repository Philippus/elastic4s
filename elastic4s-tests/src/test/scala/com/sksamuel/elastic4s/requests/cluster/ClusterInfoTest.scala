package com.sksamuel.elastic4s.requests.cluster

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.BeforeAndAfterAll
import org.scalatest.PartialFunctionValues._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ClusterInfoTest extends AnyWordSpec with Matchers with DockerTests with BeforeAndAfterAll {

  override protected def afterAll(): Unit = {
    client.execute {
      addRemoteClusterRequest(Map(
        "cluster.remote.cluster_one.seeds" -> null,
        "cluster.remote.cluster_two.seeds" -> null
      ))
    }.await
  }

  client.execute {
    addRemoteClusterRequest(Map(
      "cluster.remote.cluster_one.seeds" -> "127.0.0.1:9300, 127.0.0.2:9300",
      "cluster.remote.cluster_two.seeds" -> "127.0.0.3:9300"
    ))
  }.await

  "remote cluster info request" should {
    "return remote cluster information" in {

      val info = client.execute {
        remoteClusterInfo()
      }.await.result

      info.valueAt("cluster_one") should have(
        Symbol("seeds")(Seq("127.0.0.1:9300", "127.0.0.2:9300")),
        Symbol("maxConnectionsPerCluster")(3),
        Symbol("initialConnectTimeout")("30s"),
        Symbol("skipUnavailable")(true)
      )

      info.valueAt("cluster_two") should have(
        //  Symbol("connected") (false),
        //  Symbol("numNodesConnected") (0),
        Symbol("maxConnectionsPerCluster")(3),
        Symbol("initialConnectTimeout")("30s"),
        Symbol("skipUnavailable")(true)
      )
    }
  }

}
