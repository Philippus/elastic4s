package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import com.sksamuel.elastic4s.testkit.ElasticSugar

/** @author Stephen Samuel */
class CountDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a count request" should "accept tuple for from" in {
    val req = count from "places" -> "cities" query "name" -> "sammy"
    assert(req.build.indices() === Array("places"))
    assert(req.build.types() === Array("cities"))
  }

  it should "accept indextype" in {
    val req = count from "places" / "cities" query "name" -> "sammy"
    assert(req.build.indices() === Array("places"))
    assert(req.build.types() === Array("cities"))
  }

  it should "accept sequence of indexes and types" in {
    val req = count from Seq("index1", "index2") types Seq("type1", "type2") query " name" -> " sammy"
    assert(req.build.indices() === Array("index1", "index2"))
  }

  it should "accept sequence of indexes and single type" in {
    val req = count from Seq("index1", "index2") types "type1" query " name" -> " sammy"
    assert(req.build.indices() === Array("index1", "index2"))
  }

  it should "accept single index and single type" in {
    val req = count from "places" types "cities" query "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept single index and sequence of types" in {
    val req = count from "places" types Seq("type1", "type2") query "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept varargs index and varargs of types" in {
    val req = count from ("places", "bands") types ("type1", "type2") query "paris"
    assert(req.build.indices() === Array("places", "bands"))
  }

  it should "accept single index and varargs of types" in {
    val req = count from "places" types ("type1", "type2") query "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "parse slash indextype" in {
    val req = count from "places/cities" query "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "parase method invocation as index type" in {
    val req = countFrom("places/cities") query "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept vararg method invocation as indexes" in {
    val req = countFrom("places", "bands") query "paris"
    assert(req.build.indices() === Array("places", "bands"))
  }

  it should "accept tuple method invocation" in {
    val req = countFrom("places" -> "bands") query "paris"
    assert(req.build.indices() === Array("places"))
  }
}
