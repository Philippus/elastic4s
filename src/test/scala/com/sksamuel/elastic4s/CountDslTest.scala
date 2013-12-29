package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class CountDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a count request" should "accept tuple for from" in {
    val req = count from "places" -> "cities" where "name" -> "sammy"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept sequence of indexes and types" in {
    val req = count from Seq("index1", "index2") types Seq("type1", "type2") where " name" -> " sammy"
    assert(req.build.indices() === Array("index1", "index2"))
  }

  it should "accept sequence of indexes and single type" in {
    val req = count from Seq("index1", "index2") types "type1" where " name" -> " sammy"
    assert(req.build.indices() === Array("index1", "index2"))
  }

  it should "accept single index and single type" in {
    val req = count from "places" types "cities" where "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept single index and sequence of types" in {
    val req = count from "places" types Seq("type1", "type2") query "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept varargs index and varargs of types" in {
    val req = count from("places", "bands") types("type1", "type2") where "paris"
    assert(req.build.indices() === Array("places", "bands"))
  }

  it should "accept single index and varargs of types" in {
    val req = count from "places" types("type1", "type2") where "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "parse slash indextype" in {
    val req = count from "places/cities" query "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "parase method invocation as index type" in {
    val req = count("places/cities") query "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept vararg method invocation as indexes" in {
    val req = count("places", "bands") query "paris"
    assert(req.build.indices() === Array("places", "bands"))
  }

  it should "accept tuple method invocation" in {
    val req = count("places" -> "bands") query "paris"
    assert(req.build.indices() === Array("places"))
  }
}
