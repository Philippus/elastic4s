package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.index.query.functionscore.{RandomScoreFunctionBuilder, ScoreFunctionBuilders}

case class RandomScoreFunctionDefinition(seed: Long,
                                         weight: Option[Double] = None) extends ScoreFunctionDefinition {
  override type B = RandomScoreFunctionBuilder

  def builder = {
    val builder = ScoreFunctionBuilders.randomFunction(seed)
    weight.map(_.toFloat).foreach(builder.setWeight)
    builder
  }

  def weight(weight: Double): RandomScoreFunctionDefinition = copy(weight = weight.some)
}
