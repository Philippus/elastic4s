package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.FlatSpec
import org.scalatest.mockito.MockitoSugar

class ForceMergeApiTest extends FlatSpec with MockitoSugar with ElasticSugar {

  // not testing the output of these; they are here just to test the DSL in the client
  client.execute {
    create index "test1"
  }.await
  client.execute {
    create index "test2"
  }.await
  client.execute {
    forceMerge("test1")
  }.await
  client.execute {
    forceMerge("test1", "test2")
  }.await
  client.execute {
    forceMerge(Seq("test1", "test2"))
  }.await
  client.execute {
    forceMerge("test1")
  }.await

  "an optimize request" should "accept var args" in {
    val opt = forceMerge("index1", "index2").maxSegments(5)
  }

  it should "accept single index version" in {
    val opt = forceMerge("index1") flush true
  }

  it should "accept single seq" in {
    val opt = forceMerge(Seq("index1", "index2")) flush true
  }

  it should "accept var arg method invocation" in {
    val opt = forceMerge("index1", "index2") flush true
  }

  it should "accept single string method invocation" in {
    val opt = forceMerge("index1") flush true
  }
}
