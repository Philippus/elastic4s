package com.sksamuel.elastic4s.http.bulk

import com.sksamuel.elastic4s.RefreshPolicy
import org.scalatest.{FunSuite, Matchers}

class BulkBuilderFnTest extends FunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticApi._

  test("bulk content builder should support mixed requests") {
    val req = bulk(
      update("2").in("chemistry/elements").doc("atomicweight" -> 2, "name" -> "helium"),
      indexInto("chemistry/elements").fields("atomicweight" -> 8, "name" -> "oxygen").withId("8"),
      update("6").in("chemistry/elements").doc("atomicweight" -> 4, "name" -> "lithium"),
      delete("10").from("chemistry/elements")
    ).refresh(RefreshPolicy.Immediate)

    BulkBuilderFn(req).mkString("\n") shouldBe
      """{"update":{"_index":"chemistry","_type":"elements","_id":"2"}}
        |{"doc":{"atomicweight":2,"name":"helium"}}
        |{"index":{"_index":"chemistry","_type":"elements","_id":"8"}}
        |{"atomicweight":8,"name":"oxygen"}
        |{"update":{"_index":"chemistry","_type":"elements","_id":"6"}}
        |{"doc":{"atomicweight":4,"name":"lithium"}}
        |{"delete":{"_index":"chemistry","_type":"elements","_id":"10"}}""".stripMargin

  }

  test("bulk content builder should respect createOnly in IndexRequest") {
    val req = bulk(
      indexInto("chemistry/elements").fields("atomicweight" -> 8, "name" -> "oxygen").withId("8"),
      indexInto("chemistry/elements").fields("atomicweight" -> 1, "name" -> "hydrogen").withId("1").createOnly(true)
    ).refresh(RefreshPolicy.Immediate)

    BulkBuilderFn(req).mkString("\n") shouldBe
      """{"index":{"_index":"chemistry","_type":"elements","_id":"8"}}
        |{"atomicweight":8,"name":"oxygen"}
        |{"create":{"_index":"chemistry","_type":"elements","_id":"1"}}
        |{"atomicweight":1,"name":"hydrogen"}""".stripMargin

  }
}
