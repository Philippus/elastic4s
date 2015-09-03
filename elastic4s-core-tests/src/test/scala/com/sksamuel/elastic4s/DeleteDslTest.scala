package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.index.VersionType
import com.sksamuel.elastic4s.testkit.ElasticSugar

/** @author Stephen Samuel */
class DeleteDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a delete by id request" should "accept tuple for from" in {
    val req = delete id 141212 from "places" -> "cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "accept sequence of indexes and types" in {
    val req = delete id 141212 from Seq("index1", "index2") types Seq("type1", "type2")
    assert(req.build.index() === "index1")
    assert(req.build.`type`() === "type1")
  }

  it should "accept sequence of indexes and single type" in {
    val req = delete id 141212 from Seq("index1", "index2") types "type1"
    assert(req.build.index() === "index1")
    assert(req.build.`type`() === "type1")
  }

  it should "parse slash indextype" in {
    val req = delete id 141212 from "index/type"
    assert(req.build.index() === "index")
    assert(req.build.`type`() === "type")
  }

  it should "accept varargs index and varargs of types" in {
    val req = delete id 141212 from ("places", "bands") types ("type1", "type2")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "type1")
  }

  it should "accept single index and single type" in {
    val req = delete id 141212 from "places" types "type1"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "type1")
  }

  it should "accept routing key" in {
    val req = delete id 141212 from "places" types "type1" routing "my-route"
    assert(req.build.routing() === "my-route")
  }

  it should "accept version and version type" in {
    val req = delete id 141212 from "places" types "type1" version 53423 versionType VersionType.EXTERNAL
    assert(req.build.version() === 53423)
    assert(req.build.versionType() === VersionType.EXTERNAL)
  }

  it should "accept refresh" in {
    val req = delete id 141212 from "places" types "type1" refresh true
    assert(req.build.refresh() === true)
  }
}
