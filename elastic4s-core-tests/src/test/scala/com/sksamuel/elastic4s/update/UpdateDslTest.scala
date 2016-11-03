package com.sksamuel.elastic4s.update

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.{NestedFieldValue, SimpleFieldValue}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Entry, FlatSpec, Matchers, OneInstancePerTest}

class UpdateDslTest
  extends FlatSpec with MockitoSugar with OneInstancePerTest with Matchers with TypeCheckedTripleEquals {

  val mapper = new ObjectMapper()

  "the update dsl" should "should support retry on conflict" in {
    val updateDef = update id 5 in "scifi/startrek" retryOnConflict 4
    assert(updateDef.build.retryOnConflict() == 4)
  }

  it should "should support routing" in {
    val updateDef = update id 5 in "scifi/startrek" routing "aroundwego"
    assert(updateDef.build.routing() === "aroundwego")
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

  it should "should support docAsUpsert with explicit field types" in {
    val updateDef = update id 14 in "scifi/startrek" docAsUpsert {
      NestedFieldValue("captain", Seq(SimpleFieldValue("james", "kirk")))
    }
    val sourceMap: util.Map[String, AnyRef] = updateDef.build.doc().sourceAsMap()
    sourceMap should contain key "captain"
    sourceMap.get("captain").asInstanceOf[util.Map[String, String]] should contain(Entry("james", "kirk"))
  }

  it should "should support docAsUpsert with nested explicit field types" in {
    val updateDef = update id 14 in "scifi/startrek" docAsUpsert (
      "captain" -> SimpleFieldValue("james", "kirk")
      )
    val sourceMap: util.Map[String, AnyRef] = updateDef.build.doc().sourceAsMap()
    sourceMap should contain key "captain"
    sourceMap.get("captain").asInstanceOf[util.Map[String, String]] should contain(Entry("james", "kirk"))
  }

  it should "should support docAsUpsert with complex nested explicit field types" in {
    val updateDef = update id 14 in "scifi/startrek" docAsUpsert (
      "captain" -> NestedFieldValue("first",
        Seq(SimpleFieldValue("captain", "archer"), SimpleFieldValue("program", "NX test")))
      )
    val sourceMap: util.Map[String, AnyRef] = updateDef.build.doc().sourceAsMap()
    sourceMap.get("captain").asInstanceOf[util.Map[String, String]].get("first")
      .asInstanceOf[util.Map[String, String]] should contain(Entry("captain", "archer"))
  }

  it should "accept tuple for in" in {
    (update id 65 in "places" -> "cities").build.index === "places"
    (update id 65 in "places" -> "cities").build.`type` === "cities"
    update(65).in("places" -> "cities").build.index === "places"
    update(65).in("places" -> "cities").build.`type` === "cites"
  }

  it should "accept two parameters for in" in {
    (update id 65 in("places", "cities")).build.index === "places"
    (update id 65 in("places", "cities")).build.`type` === "cities"
    update(65).in("places", "cities").build.index === "places"
    update(65).in("places", "cities").build.`type` === "cities"
  }

  it should "parse slash indextype" in {
    val req = update id 65 in "places/cities"
    req.build.index === "places"
    req.build.`type` === "cities"
    update(65).in("places/cities").build.index === "places"
    update(65).in("places/cities").build.`type` === "cities"
  }
}