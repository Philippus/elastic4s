package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{GaussDecayFunctionBuilder, ScoreFunctionBuilders}
import org.elasticsearch.search.MultiValueMode
import com.sksamuel.exts.OptionImplicits._

case class GaussianDecayScoreDefinition(field: String,
                                        origin: String,
                                        scale: String,
                                        offset: Option[Any] = None,
                                        decay: Option[Double] = None,
                                        weight: Option[Double] = None,
                                        multiValueMode: Option[MultiValueMode] = None) extends ScoreFunctionDefinition {

  override type B = GaussDecayFunctionBuilder

  def builder = {
    val builder = (offset, decay) match {
      case (Some(o), Some(d)) => ScoreFunctionBuilders.gaussDecayFunction(field, origin, scale, o, d)
      case (Some(o), None) => ScoreFunctionBuilders.gaussDecayFunction(field, origin, scale, o)
      case _ => ScoreFunctionBuilders.gaussDecayFunction(field, origin, scale)
    }
    weight.map(_.toFloat).foreach(builder.setWeight)
    multiValueMode.foreach(builder.setMultiValueMode)
    builder
  }

  def multiValueMode(multiValueMode: MultiValueMode): GaussianDecayScoreDefinition =
    copy(multiValueMode = multiValueMode.some)

  def weight(weight: Double): GaussianDecayScoreDefinition = copy(weight = weight.some)
  def decay(decay: Double): GaussianDecayScoreDefinition = copy(decay = decay.some)
  def offset(offset: Any): GaussianDecayScoreDefinition = copy(offset = offset.some)
}
