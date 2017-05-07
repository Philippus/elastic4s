package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{FlatSpec, Matchers}

class CatPluginsTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "cats plugins" should "return all plugins" in {
    http.execute {
      catPlugins()
    }.await.exists(_.component == "org.elasticsearch.index.reindex.ReindexPlugin") shouldBe true
  }
}
