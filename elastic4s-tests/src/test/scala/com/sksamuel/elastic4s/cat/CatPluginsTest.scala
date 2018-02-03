package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class CatPluginsTest extends FlatSpec with Matchers with DockerTests {

  "cats plugins" should "return all plugins" in {

    val result = http.execute {
      catPlugins()
    }.await.result

    result.map(_.component) shouldBe Seq("ingest-geoip", "ingest-user-agent")
  }
}
