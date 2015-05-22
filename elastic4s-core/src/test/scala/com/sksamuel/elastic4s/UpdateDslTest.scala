package com.sksamuel.elastic4s

import org.scalatest.{ Entry, Matchers, FlatSpec, OneInstancePerTest }
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.support.replication.ReplicationType
import com.sksamuel.elastic4s.source.DocumentSource
import java.util

/** @author Stephen Samuel */
class UpdateDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest with Matchers {

  val mapper = new ObjectMapper()

  "the update dsl" should "should support retry on conflict" in {
    val updateDef = update id 5 in "scifi/startrek" retryOnConflict 4
    assert(updateDef.build.retryOnConflict() == 4)
  }

  it should "should support consistencyLevel" in {
    val updateDef = update id 5 in "scifi/startrek" consistencyLevel WriteConsistencyLevel.ONE
    assert(updateDef.build.consistencyLevel() == WriteConsistencyLevel.ONE)
  }

  it should "should support lang" in {
    val updateDef = update id 5 in "scifi/startrek" lang "welsh"
    assert(updateDef.build.scriptLang() === "welsh")
  }

  it should "should support routing" in {
    val updateDef = update id 5 in "scifi/startrek" routing "aroundwego"
    assert(updateDef.build.routing() === "aroundwego")
  }

  it should "should support replicationType" in {
    val updateDef = update id 5 in "scifi/startrek" replicationType ReplicationType.ASYNC
    assert(updateDef.build.replicationType() === ReplicationType.ASYNC)
  }

  it should "should support docAsUpdate" in {
    val updateDef = update id 14 in "scifi/startrek" docAsUpsert true
    assert(updateDef.build.docAsUpsert())
  }

  it should "should support docAsUpsert with nested object" in {
    val updateDef = update id 14 in "scifi/startrek" docAsUpsert (
      "captain" -> Map("james" -> "kirk")
    )
    val sourceMap: util.Map[String, AnyRef] = updateDef.build.doc().sourceAsMap()
    sourceMap should contain key "captain"
    sourceMap.get("captain").asInstanceOf[util.Map[String, String]] should contain(Entry("james", "kirk"))
  }

  it should "should support source" in {
    val updateDef = update id 65 in "scifi/startrek" doc new TestSource
    assert(updateDef.build.doc().sourceAsMap().containsKey("ship"))
    assert(updateDef.build.doc().sourceAsMap().containsValue("enterprise"))
  }

  it should "accept tuple for in" in {
    val req = update id 65 in "places" -> "cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "accept two parameters for in" in {
    val req = update id 65 in ("places", "cities")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "parse slash indextype" in {
    val req = update id 65 in "places/cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }
}

case class TestSource() extends DocumentSource {
  def json: String = """{ "ship" : "enterprise"}"""
}

