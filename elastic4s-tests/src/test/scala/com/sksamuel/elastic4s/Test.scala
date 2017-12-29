package com.sksamuel.elastic4s

import java.nio.file.Files

import com.sksamuel.elastic4s.embedded.LocalNode

object Test extends App {

  import com.sksamuel.elastic4s.http.ElasticDsl._

  val clusterName = "getting-started-with-elastic4s"
  val homePath = Files.createTempDirectory(clusterName)
  val localNode = LocalNode(clusterName, homePath.toAbsolutePath.toString)
  localNode.start()

  val client = localNode.http(true)
  val resp = client.execute {
    createIndex("trial")
  }.await
  println(resp)

}
