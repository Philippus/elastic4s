package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ClassloaderLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class CatAllocationTest extends FlatSpec with Matchers with ClassloaderLocalNodeProvider with ElasticDsl {

  http.execute {
    bulk(
      indexInto("catalloc1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catalloc2/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catalloc3/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await


  "cats alloc" should "return all shards" in {
    http.execute {
      catAllocation()
    }.await
  }

}
