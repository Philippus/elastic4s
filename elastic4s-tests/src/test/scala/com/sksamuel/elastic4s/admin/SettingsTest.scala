package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.analyzers.WhitespaceAnalyzer
import org.scalatest.WordSpec
import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticSugar}

class SettingsTest extends WordSpec with ElasticSugar with ElasticDsl with ClassloaderLocalNodeProvider {

  client.execute {
    createIndex("settings_test").mappings(
      mapping("r").as(
        textField("a") stored true analyzer WhitespaceAnalyzer,
        longField("b")
      )
    )
  }.await

  "get settings" should {
    "return settings" in {

      val resp = client.execute {
        getSettings("settings_test")
      }.await

      val settings = resp.getIndexToSettings.get("settings_test")
      // default values
      assert(settings.getAsSettings("index").get("number_of_shards") === "5")
      assert(settings.getAsSettings("index").get("number_of_replicas") === "1")
    }
  }
  "put settings" should {
    "update settings" in {

      client.execute {
        updateSettings("settings_test").set(Map("index.refresh_interval" -> "10s"))
      }.await

      val resp = client.execute {
        getSettings("settings_test")
      }.await

      val refresh_interval = resp.getSetting("settings_test", "index.refresh_interval")
      assert(refresh_interval === "10s")
    }
  }
}
