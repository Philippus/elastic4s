package com.sksamuel.elastic4s.requests.bulk

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import org.scalatest.{FunSuite, Matchers}

class BulkBuilderFnTest extends FunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticApi._

  test("bulk content builder should support mixed requests") {
    val req = bulk(
      update("2").in("chemistry").doc("atomicweight" -> 2, "name" -> "helium"),
      indexInto("chemistry").fields("atomicweight" -> 8, "name" -> "oxygen").withId("8"),
      update("6").in("chemistry").doc("atomicweight" -> 4, "name" -> "lithium"),
      delete("10").from("chemistry"),
      indexInto("chemistry").fields("atomicweight" -> 81, "name" -> "thallium").withId("14").pipeline("periodic-table")
    ).refresh(RefreshPolicy.Immediate)

    BulkBuilderFn(req).mkString("\n") shouldBe
      """{"update":{"_index":"chemistry","_id":"2"}}
        |{"doc":{"atomicweight":2,"name":"helium"}}
        |{"index":{"_index":"chemistry","_id":"8"}}
        |{"atomicweight":8,"name":"oxygen"}
        |{"update":{"_index":"chemistry","_id":"6"}}
        |{"doc":{"atomicweight":4,"name":"lithium"}}
        |{"delete":{"_index":"chemistry","_id":"10"}}
        |{"index":{"_index":"chemistry","_id":"14","pipeline":"periodic-table"}}
        |{"atomicweight":81,"name":"thallium"}""".stripMargin

  }

  test("bulk content builder should respect createOnly in IndexRequest") {
    val req = bulk(
      indexInto("chemistry").fields("atomicweight" -> 8, "name" -> "oxygen").withId("8"),
      indexInto("chemistry").fields("atomicweight" -> 1, "name" -> "hydrogen").withId("1").createOnly(true)
    ).refresh(RefreshPolicy.Immediate)

    BulkBuilderFn(req).mkString("\n") shouldBe
      """{"index":{"_index":"chemistry","_id":"8"}}
        |{"atomicweight":8,"name":"oxygen"}
        |{"create":{"_index":"chemistry","_id":"1"}}
        |{"atomicweight":1,"name":"hydrogen"}""".stripMargin

  }

  test("bulk content builder should support retry_on_conflict in UpdateRequest") {
    val req = bulk(
      update("2").in("chemistry").doc("atomicweight" -> 2, "name" -> "helium").retryOnConflict(3)
    )

    BulkBuilderFn(req).mkString("\n") shouldBe
      """{"update":{"_index":"chemistry","_id":"2","retry_on_conflict":3}}
        |{"doc":{"atomicweight":2,"name":"helium"}}""".stripMargin
  }
}
