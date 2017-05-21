package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ClassloaderLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class CatPluginsTest extends FlatSpec with Matchers with ClassloaderLocalNodeProvider with ElasticDsl {

  "cats plugins" should "return all plugins" in {
    http.execute {
      catPlugins()
    }.await.exists(_.component == "org.elasticsearch.index.reindex.ReindexPlugin") shouldBe true
  }
}
