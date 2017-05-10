package com.sksamuel.elastic4s.samples

import com.sksamuel.elastic4s.{ElasticsearchClientUri, TcpClient}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

object TcpClientExampleApp extends App {

  // you must import the DSL to use the syntax helpers
  import com.sksamuel.elastic4s.ElasticDsl._

  val client = TcpClient.transport(ElasticsearchClientUri("localhost", 9300))

  client.execute {
    bulk(
      indexInto("myindex" / "mytype").fields("country" -> "Mongolia", "capital" -> "Ulaanbaatar"),
      indexInto("myindex" / "mytype").fields("country" -> "Namibia", "capital" -> "Windhoek")
    ).refresh(RefreshPolicy.WAIT_UNTIL)
  }.await

  val result = client.execute {
    search("myindex").matchQuery("capital", "ulaanbaatar")
  }.await

  // prints out the original json
  println(result.hits.head.sourceAsString)

  client.close()

}
