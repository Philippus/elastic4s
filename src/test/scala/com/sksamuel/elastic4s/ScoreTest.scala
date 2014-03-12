package com.sksamuel.elastic4s

import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, OneInstancePerTest}
import com.sksamuel.elastic4s.ElasticDsl._
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.common.xcontent.{ToXContent, XContentFactory}

/** @author Stephen Samuel */
class ScoreTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

  val mapper = new ObjectMapper()

  "a score dsl" should "generate correct json for a linear decay function scorer" in {
    val req = linearScore("myfield", "1 2", "2km").offset(100).decay(0.1)
    val json = mapper.readTree(getClass.getResource("/json/score/score_linear.json"))
    val actual = req.builder.toXContent(XContentFactory.jsonBuilder().startObject(), ToXContent.EMPTY_PARAMS).string()
    assert(json === mapper.readTree(actual))
  }

  it should "generate correct json for a gaussian decay function scorer" in {
    val req = gaussianScore("myfield", "1 2", "3km").offset("1km").decay(0.2)
    val json = mapper.readTree(getClass.getResource("/json/score/score_gaussian.json"))
    val actual = req.builder.toXContent(XContentFactory.jsonBuilder().startObject(), ToXContent.EMPTY_PARAMS).string()
    assert(json === mapper.readTree(actual))
  }

  it should "generate correct json for an exponential decay function scorer" in {
    val req = exponentialScore("myfield", "1 2", "4km").offset(100).decay(0.4)
    val json = mapper.readTree(getClass.getResource("/json/score/score_exponential.json"))
    val actual = req.builder.toXContent(XContentFactory.jsonBuilder().startObject(), ToXContent.EMPTY_PARAMS).string()
    assert(json === mapper.readTree(actual))
  }

  it should "generate correct json for a random function scorer" in {
    val req = randomScore(12345)
    val json = mapper.readTree(getClass.getResource("/json/score/score_random.json"))
    val actual = req.builder.toXContent(XContentFactory.jsonBuilder().startObject(), ToXContent.EMPTY_PARAMS).string()
    assert(json === mapper.readTree(actual))
  }

  it should "generate correct json for a script scorer" in {
    val req = scriptScore("some script").lang("java").param("param1", "value1").params(Map("param2" -> "value2"))
    val json = mapper.readTree(getClass.getResource("/json/score/score_script.json"))
    val actual = req.builder.toXContent(XContentFactory.jsonBuilder().startObject(), ToXContent.EMPTY_PARAMS).string()
    assert(json === mapper.readTree(actual))
  }
}
