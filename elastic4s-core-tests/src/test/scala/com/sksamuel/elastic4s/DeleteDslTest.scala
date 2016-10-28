package com.sksamuel.elastic4s2

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{Matchers, FlatSpec}
import ElasticDsl._
import org.elasticsearch.index.VersionType
import com.sksamuel.elastic4s.testkit.ElasticSugar

/** @author Stephen Samuel */
class DeleteDslTest extends FlatSpec with Matchers with ElasticSugar with TypeCheckedTripleEquals {

  "a delete by id request" should "accept tuple for from" in {
    val req = delete id 141212 from "places" -> "cities"
    req.build.index === "places"
    req.build.`type` === "cities"
  }

  it should "parse slash indextype" in {
    val req = delete id 141212 from "index/type"
    req.build.index === "index"
    req.build.`type` === "type"
  }

  it should "accept varargs index and and a type" in {
    val req = delete id 141212 from("places", "bands") `type` "type1"
    req.build.index === "places"
    req.build.`type` === "type1"
  }

  it should "accept single index and single type" in {
    val req = delete id 141212 from "places" `type` "type1"
    req.build.index === "places"
    req.build.`type` === "type1"
  }

  it should "accept index and type in dot syntax" in {
    delete(123).from("places", "type1").build.index === "places"
    delete(123).from("places", "type1").build.`type` === "type1"
  }

  it should "accept tuple in dot syntax" in {
    delete(123).from("places" -> "type1").build.index === "places"
    delete(123).from("places" -> "type1").build.`type` === "type1"
  }

  it should "accept routing key" in {
    delete(141212).from("places").`type`("type1").routing("my-route").build.routing === "my-route"
  }

  it should "accept version and version type" in {
    val req = delete id 141212 from "places" `type` "type1" version 53423l versionType VersionType.EXTERNAL
    assert(req.build.version() === 53423l)
    assert(req.build.versionType() === VersionType.EXTERNAL)
  }

  it should "accept refresh" in {
    (delete id 141212 from "places" `type` "type1" refresh true).build.refresh() === true
  }
}
