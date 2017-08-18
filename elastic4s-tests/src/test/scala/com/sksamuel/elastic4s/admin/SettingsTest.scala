//package com.sksamuel.elastic4s.admin
//
//import com.sksamuel.elastic4s.analyzers.WhitespaceAnalyzer
//import com.sksamuel.elastic4s.http.ElasticDsl
//import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
//import org.scalatest.WordSpec
//
//class SettingsTest extends WordSpec with ElasticDsl with DiscoveryLocalNodeProvider {
//
//  http.execute {
//    createIndex("settings_test").mappings(
//      mapping("r").as(
//        textField("a") stored true analyzer WhitespaceAnalyzer,
//        longField("b")
//      )
//    )
//  }.await
//
//  "get settings" should {
//    "return settings" in {
//
//      val resp = http.execute {
//        getSettings("settings_test")
//      }.await
//
//      val settings = resp.getIndexToSettings.get("settings_test")
//      // default values
//      assert(settings.getAsSettings("index").get("number_of_shards") === "5")
//      assert(settings.getAsSettings("index").get("number_of_replicas") === "1")
//    }
//  }
//  "put settings" should {
//    "update settings" in {
//
//      http.execute {
//        updateSettings("settings_test").set(Map("index.refresh_interval" -> "10s"))
//      }.await
//
//      val resp = http.execute {
//        getSettings("settings_test")
//      }.await
//
//      val refresh_interval = resp.getSetting("settings_test", "index.refresh_interval")
//      assert(refresh_interval === "10s")
//    }
//  }
//}
