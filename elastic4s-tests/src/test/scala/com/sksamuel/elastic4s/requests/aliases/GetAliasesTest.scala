package com.sksamuel.elastic4s.requests.aliases

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.handlers.alias.{Alias, IndexAliases}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GetAliasesTest extends AnyFunSuite with Matchers with DockerTests {

  test("get all aliases") {
    client.execute {
      createIndex("feed_1_22222223")
    }.await

    client.execute {
      createIndex("feed_1_2222222345")
    }.await

    client.execute {
      createIndex("feed_1_222222234")
    }.await

    client.execute {
      createIndex("feed_1_1537175433991").alias("feed_1_sync")
    }.await

    client.execute {
      createIndex("feed_11759_1533130917711").alias("feed_11759_sync")
    }.await

    client.execute {
      createIndex("feed_1_2222222")
    }.await

    client.execute {
      getAliases("feed*", Nil)
    }.await.result shouldBe IndexAliases(
      Map(
        Index("feed_1_22222223") -> Nil,
        Index("feed_1_2222222345") -> Nil,
        Index("feed_1_222222234") -> Nil,
        Index("feed_1_1537175433991") -> List(Alias("feed_1_sync")),
        Index("feed_11759_1533130917711") -> List(Alias("feed_11759_sync")),
        Index("feed_1_2222222") -> Nil
      )
    )
  }
}
