package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import scala.concurrent.duration.DurationInt

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.concurrent.Eventually
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class IlmTest extends AnyFlatSpec with Matchers with ElasticDsl with DockerTests with Eventually {
   "ilm" should "return status" in {
     client.execute {
       startIlm()
     }.await.result

     eventually(timeout(5.seconds)) {
       val resp = client.execute {
         getIlmStatus
       }.await.result
       resp.operationMode shouldBe "RUNNING"
     }
  }

  "ilm" should "stop" in {
    val resp = client.execute {
      stopIlm()
    }.await.result
    resp.acknowledged shouldBe true

    eventually(timeout(5.seconds)) {
      val resp = client.execute {
        getIlmStatus
      }.await.result
      resp.operationMode shouldBe "STOPPED"
    }
  }

  "ilm" should "start" in {
    client.execute {
      stopIlm()
    }.await.result

    eventually(timeout(5.seconds)) {
      val resp = client.execute {
        getIlmStatus
      }.await.result
      resp.operationMode shouldBe "STOPPED"
    }

    val resp = client.execute {
      startIlm()
    }.await.result
    resp.acknowledged shouldBe true

    eventually(timeout(5.seconds)) {
      val resp = client.execute {
        getIlmStatus
      }.await.result
      resp.operationMode shouldBe "RUNNING"
    }
  }
}
