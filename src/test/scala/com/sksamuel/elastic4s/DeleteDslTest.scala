package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.index.VersionType
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.action.WriteConsistencyLevel

/** @author Stephen Samuel */
class DeleteDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a delete by query request" should "accept tuple for from" in {
    val req = delete from "places" -> "cities" where "name" -> "sammy"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept sequence of indexes and types" in {
    val req = delete from Seq("index1", "index2") types Seq("type1", "type2") where " name" -> " sammy"
    assert(req.build.indices() === Array("index1", "index2"))
  }

  it should "accept sequence of indexes and single type" in {
    val req = delete from Seq("index1", "index2") types "type1" where " name" -> " sammy"
    assert(req.build.indices() === Array("index1", "index2"))
  }

  it should "accept single index and single type" in {
    val req = delete from "places" types "cities" where "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept single index and sequence of types" in {
    val req = delete from "places" types Seq("type1", "type2") where "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept varargs index and varargs of types" in {
    val req = delete from ("places", "bands") types ("type1", "type2") where "paris"
    assert(req.build.indices() === Array("places", "bands"))
  }

  it should "accept single index and varargs of types" in {
    val req = delete from "places" types ("type1", "type2") where "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "parse slash indextype" in {
    val req = delete from "places/cities" where "paris"
    assert(req.build.indices() === Array("places"))
  }

  it should "accept routing key" in {
    val req = delete from "places/cities" where "paris" routing "my-route"
    assert(req.build.routing() === "my-route")
  }

  it should "accept replication type" in {
    val req = delete from "places/cities" where "paris" replicationType ReplicationType.SYNC
    assert(req.build.replicationType() === ReplicationType.SYNC)
  }

  it should "accept consistency level" in {
    val req = delete from "places/cities" where "paris" consistencyLevel WriteConsistencyLevel.ONE
    assert(req.build.consistencyLevel() === WriteConsistencyLevel.ONE)
  }

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
