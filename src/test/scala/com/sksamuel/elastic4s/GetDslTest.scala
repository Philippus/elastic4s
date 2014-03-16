package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import ElasticDsl._

/** @author Stephen Samuel */
class GetDslTest extends FlatSpec with Matchers with ElasticSugar {

  "a get by id request" should "accept tuple for from" in {
    val req = get id 123 from "places" -> "cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "accept two parameters" in {
    val req = get id 123 from ("places", "cities")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "parse slash indextype" in {
    val req = get id 123 from "places/cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "accept one field" in {
    val req = get id 123 from "places/cities" fields ("name")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
    req.build.fields() should equal(Array("name"))
  }

  it should "accept multiple fields" in {
    val req = get id 123 from "places/cities" fields ("name", "title", "content")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
    req.build.fields() should equal(Array("name", "title", "content"))
  }

  it should "disable fetchSource" in {
    val req = get id 123 from "places/cities" fetchSourceContext (false)
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
    req.build.fields() should be(null)
    req.build.fetchSourceContext().fetchSource should be(false)
  }
}
