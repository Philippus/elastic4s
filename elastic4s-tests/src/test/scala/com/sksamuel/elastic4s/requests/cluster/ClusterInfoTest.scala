package com.sksamuel.elastic4s.requests.cluster

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.BeforeAndAfterAll
import org.scalatest.PartialFunctionValues._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ClusterInfoTest extends AnyWordSpec with Matchers with DockerTests with BeforeAndAfterAll {

  override protected def afterAll(): Unit = {
    client.execute {
      addRemoteClusterRequest(Map("search.remote.cluster_one.seeds" -> null, "search.remote.cluster_two.seeds" -> null))
    }.await
  }

  client.execute {
    addRemoteClusterRequest(Map(
      "search.remote.cluster_one.seeds" -> "127.0.0.1:9300, 127.0.0.2:9300",
      "search.remote.cluster_two.seeds" -> "127.0.0.3:9300"))
  }.await

  "remote cluster info request" should {
    "return remote cluster information" in {

      val info = client.execute {
        remoteClusterInfo()
      }.await.result

      info.valueAt("cluster_one") should have(
        'seeds (Seq("127.0.0.1:9300", "127.0.0.2:9300")),
        'maxConnectionsPerCluster (3),
        'initialConnectTimeout ("30s"),
        'skipUnavailable (false)
      )

      info.valueAt("cluster_two") should have(
      //  'connected (false),
      //  'numNodesConnected (0),
        'maxConnectionsPerCluster (3),
        'initialConnectTimeout ("30s"),
        'skipUnavailable (false))
    }
  }

}
