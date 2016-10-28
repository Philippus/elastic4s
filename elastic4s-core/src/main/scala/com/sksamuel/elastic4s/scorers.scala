package com.sksamuel.elastic4s

import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction
import org.elasticsearch.index.query.functionscore._

trait ScoreDsl {

  def randomScore(seed: Int) = RandomScoreDefinition(seed)

  def scriptScore(script: ScriptDefinition) = ScriptScoreDefinition(script)

  def gaussianScore(field: String, origin: String, scale: String, offset: String) =
    GaussianDecayScoreDefinition(field, origin, scale, offset)

  def linearScore(field: String, origin: String, scale: String, offset: String) =
    LinearDecayScoreDefinition(field, origin, scale, offset)

  def exponentialScore(field: String, origin: String, scale: String, offset: String) = {
    new ExponentialDecayScoreDefinition(field, origin, scale, offset)
  }

  def fieldFactorScore(field: String) = FieldValueFactorDefinition(field)

  def weightScore(boost: Double) = WeightScoreDefinition(boost)
}

case class WeightScoreDefinition(boost: Double) extends ScoreDefinition[WeightScoreDefinition] {
  val builder = new WeightBuilder().setWeight(boost.toFloat)
}

trait ScoreDefinition[T] {

  val builder: ScoreFunctionBuilder[T]
  var _filter: Option[QueryDefinition] = None

  def filter(filter: QueryDefinition): T = {
    this._filter = Option(filter)
    this.asInstanceOf[T]
  }

  def weight(boost: Double): T = {
    builder.setWeight(boost.toFloat)
    this.asInstanceOf[T]
  }
}

case class FieldValueFactorDefinition(fieldName: String) extends ScoreDefinition[FieldValueFactorDefinition] {

  override val builder = new FieldValueFactorFunctionBuilder(fieldName: String)

  def factor(f: Double): this.type = {
    builder.factor(f.toFloat)
    this
  }

  def modifier(m: FieldValueFactorFunction.Modifier): this.type = {
    builder.modifier(m)
    this
  }

  def missing(v: Double): this.type = {
    builder.missing(v)
    this
  }
}

case class RandomScoreDefinition(seed: Int) extends ScoreDefinition[RandomScoreDefinition] {
  val builder = new RandomScoreFunctionBuilder().seed(seed)
}

case class ScriptScoreDefinition(script: ScriptDefinition) extends ScoreDefinition[ScriptScoreDefinition] {
  val builder = new ScriptScoreFunctionBuilder(script.toJavaAPI)
}

abstract class DecayScoreDefinition[T] extends ScoreDefinition[T] {
  val builder: DecayFunctionBuilder[T]
}

case class GaussianDecayScoreDefinition(field: String, origin: String, scale: String, offset: String)
  extends DecayScoreDefinition[GaussianDecayScoreDefinition] {
  val builder = new GaussDecayFunctionBuilder(field, origin, scale, offset)
}

case class LinearDecayScoreDefinition(field: String, origin: String, scale: String, offset: String)
  extends DecayScoreDefinition[LinearDecayScoreDefinition] {
  val builder = new LinearDecayFunctionBuilder(field, origin, scale, offset)
}

class ExponentialDecayScoreDefinition(field: String, origin: String, scale: String, offset: String)
  extends DecayScoreDefinition[ExponentialDecayScoreDefinition] {
  val builder = new ExponentialDecayFunctionBuilder(field, origin, scale, offset)
}

