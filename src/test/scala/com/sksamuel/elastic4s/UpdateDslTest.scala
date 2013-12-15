package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.support.replication.ReplicationType
import com.sksamuel.elastic4s.source.Source

/** @author Stephen Samuel */
class UpdateDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

  val mapper = new ObjectMapper()

  "the search dsl" should "should support retry on conflict" in {
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

  it should "should support percolate" in {
    val updateDef = update id 54 in "scifi/startrek" percolate "inandout"
    assert(updateDef.build.percolate() === "inandout")
  }

  it should "should support replicationType" in {
    val updateDef = update id 5 in "scifi/startrek" replicationType ReplicationType.ASYNC
    assert(updateDef.build.replicationType() === ReplicationType.ASYNC)
  }

  it should "should support docAsUpdate" in {
    val updateDef = update id 14 in "scifi/startrek" docAsUpsert true
    assert(updateDef.build.docAsUpsert())
  }

  it should "should support source" in {
    val updateDef = update id 65 in "scifi/startrek" source new TestSource
    assert(updateDef.build.doc().sourceAsMap().containsKey("ship"))
    assert(updateDef.build.doc().sourceAsMap().containsValue("enterprise"))
  }
}

case class TestSource() extends Source {
  def json: String = """{ "ship" : "enterprise"}"""
}

