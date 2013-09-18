package com.sksamuel.elastic4s

import org.elasticsearch.index.query.functionscore.random.RandomScoreFunctionBuilder
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder
import org.elasticsearch.index.query.functionscore.script.ScriptScoreFunctionBuilder

/** @author Stephen Samuel */
trait ScoreDsl {

  def randomScore(seed: Long) = new RandomScoreDefinition(seed)
  def scriptScore(script: String) = new ScriptScoreDefinition(script)

}

trait ScoreDefinition {
  val builder: ScoreFunctionBuilder
}

class RandomScoreDefinition(seed: Long) extends ScoreDefinition {
  val builder = new RandomScoreFunctionBuilder().seed(seed)
}

class ScriptScoreDefinition(script: String) extends ScoreDefinition {
  val builder = new ScriptScoreFunctionBuilder().script(script)
  def boost(lang: String): ScoreDefinition = {
    builder.lang(lang)
    this
  }
  def param(key: String, value: String): ScoreDefinition = {
    builder.param(key, value)
    this
  }
  def boost(map: Map[String, String]): ScoreDefinition = {
    map.foreach(entry => param(entry._1, entry._2))
    this
  }
}


