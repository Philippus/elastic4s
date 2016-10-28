package com.sksamuel.elastic4s2.search.queries.funcscorer

import com.sksamuel.elastic4s2.ScriptDefinition
import com.sksamuel.elastic4s2.search.QueryDefinition
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
