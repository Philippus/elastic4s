package com.sksamuel.elastic4s

import org.elasticsearch.index.query.functionscore.random.RandomScoreFunctionBuilder
import org.elasticsearch.index.query.functionscore.{DecayFunctionBuilder, ScoreFunctionBuilder}
import org.elasticsearch.index.query.functionscore.script.ScriptScoreFunctionBuilder
import org.elasticsearch.index.query.functionscore.gauss.GaussDecayFunctionBuilder
import org.elasticsearch.index.query.functionscore.exp.ExponentialDecayFunctionBuilder
import org.elasticsearch.index.query.functionscore.lin.LinearDecayFunctionBuilder

/** @author Stephen Samuel */
trait ScoreDsl {

  def randomScore(seed: Long) = new RandomScoreDefinition(seed)
  def scriptScore(script: String) = new ScriptScoreDefinition(script)
  def gaussianScore(field: String, origin: String, scale: String) =
    new GaussianDecayScoreDefinition(field, origin, scale)
  def linearScore(field: String, origin: String, scale: String) =
    new LinearDecayScoreDefinition(field, origin, scale)
  def exponentialScore(field: String, origin: String, scale: String) =
    new ExponentialDecayScoreDefinition(field, origin, scale)
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

abstract class DecayScoreDefinition[T] extends ScoreDefinition {
  val builder: DecayFunctionBuilder
  def offset(offset: String): T = {
    builder.setOffset(offset: String)
    this.asInstanceOf[T]
  }
  def decay(decay: Double): T = {
    builder.setDecay(decay)
    this.asInstanceOf[T]
  }
}

class GaussianDecayScoreDefinition(field: String, origin: String, scale: String)
  extends DecayScoreDefinition[GaussianDecayScoreDefinition] {
  val builder = new GaussDecayFunctionBuilder(field, origin, scale)
}

class LinearDecayScoreDefinition(field: String, origin: String, scale: String)
  extends DecayScoreDefinition[LinearDecayScoreDefinition] {
  val builder = new LinearDecayFunctionBuilder(field, origin, scale)
}

class ExponentialDecayScoreDefinition(field: String, origin: String, scale: String)
  extends DecayScoreDefinition[ExponentialDecayScoreDefinition] {
  val builder = new ExponentialDecayFunctionBuilder(field, origin, scale)
}

