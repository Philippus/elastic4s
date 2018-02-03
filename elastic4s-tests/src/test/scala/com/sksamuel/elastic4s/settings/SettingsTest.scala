package com.sksamuel.elastic4s.settings

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class SettingsTest extends WordSpec with Matchers with DockerTests {

  deleteIdx("settingsa")
  deleteIdx("settingsb")
  create("settingsa")
  create("settingsb")

  def deleteIdx(name: String) = Try {
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
      val response = http.execute {
        getSettings("settingsa")
      }.await.result
      val settings = response.settingsForIndex("settingsa")
      settings("index.provided_name") shouldBe "settingsa"
      settings("index.number_of_replicas") shouldBe "1"
      settings("index.number_of_shards") shouldBe "5"
      settings("index.uuid") should not be null
    }
    "return settings from multiple indexes" in {

      val response = http.execute {
        getSettings(Seq("settingsa", "settingsb"))
      }.await.result

      val settingsa = response.settingsForIndex("settingsa")
      settingsa("index.provided_name") shouldBe "settingsa"
      settingsa("index.number_of_replicas") shouldBe "1"
      settingsa("index.number_of_shards") shouldBe "5"
      settingsa("index.uuid") should not be null

      val settingsb = response.settingsForIndex("settingsb")
      settingsb("index.provided_name") shouldBe "settingsb"
      settingsb("index.number_of_replicas") shouldBe "1"
      settingsb("index.number_of_shards") shouldBe "5"
      settingsb("index.uuid") should not be null
    }
    "return error if index does not exist" in {
      http.execute {
        getSettings("wibble")
      }.await.error.`type` shouldBe "index_not_found_exception"
    }
  }

  "updateSettings" should {
    "override settings" in {

      http.execute {
        updateSettings("settingsa", Map("index.refresh_interval" -> "20s"))
      }.await

      val response = http.execute {
        getSettings(Seq("settingsa"))
      }.await.result

      val settings = response.settingsForIndex("settingsa")
      settings("index.refresh_interval") shouldBe "20s"
    }
  }
}
