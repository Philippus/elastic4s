package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class ElasticClientPrefixTests extends FlatSpec with Matchers with OptionValues with DockerTests {

  override protected lazy val elasticUri: String = "http://localhost:9200/prefix"

  "DefaultHttpClient" should "attempt and fail to read /prefix/testindex" in {
    val response = client.execute {
      count("textindex")
    }.await

    response.error.`type` shouldEqual "index_not_found_exception"
    response.error.index.value shouldEqual "prefix"
  }
}
