package com.sksamuel.elastic4s.settings

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class SettingsTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  delete("settingsa")
  delete("settingsb")
  create("settingsa")
  create("settingsb")

  def delete(name: String) = Try {
    http.execute {
      deleteIndex(name)
    }.await
  }

  def create(name: String) = Try {
    http.execute {
      createIndex(name)
    }.await
  }

  http.execute {
    bulk(
      indexInto("settings" / "a").fields(Map("foo" -> "bar"))
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "getSettings" should {
    "return settings from one index" in {
      http.execute {
        getSettings("settingsa")
      }.await shouldBe ""
    }
    "return settings from multiple indexes" in {
      http.execute {
        getSettings(Seq("settingsa", "settingsb"))
      }.await shouldBe ""
    }
  }
}
