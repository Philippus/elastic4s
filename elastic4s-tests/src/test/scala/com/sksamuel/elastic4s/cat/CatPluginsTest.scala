package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class CatPluginsTest extends FlatSpec with Matchers with DockerTests {

  "cats plugins" should "return all plugins" in {
    http.execute {
      catPlugins()
    }.await.right.get.result.exists(_.component == "org.elasticsearch.index.reindex.ReindexPlugin") shouldBe true
  }
}
