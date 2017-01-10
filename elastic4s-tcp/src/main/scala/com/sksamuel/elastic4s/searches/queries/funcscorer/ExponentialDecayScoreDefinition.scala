package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{ExponentialDecayFunctionBuilder, ScoreFunctionBuilders}
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.MultiValueMode

case class ExponentialDecayScoreDefinition(field: String,
                                           origin: String,
                                           scale: String,
                                           offset: Option[Any] = None,
                                           decay: Option[Double] = None,
                                           weight: Option[Double] = None,
                                           multiValueMode: Option[MultiValueMode] = None)
  extends ScoreFunctionDefinition {
  override type B = ExponentialDecayFunctionBuilder

  def builder = {
    val builder = (offset, decay) match {
      case (Some(o), Some(d)) => ScoreFunctionBuilders.exponentialDecayFunction(field, origin, scale, o, d)
      case (Some(o), None) => ScoreFunctionBuilders.exponentialDecayFunction(field, origin, scale, o)
      case _ => ScoreFunctionBuilders.exponentialDecayFunction(field, origin, scale)
    }
    weight.map(_.toFloat).foreach(builder.setWeight)
    multiValueMode.foreach(builder.setMultiValueMode)
    builder
  }

  def decay(decay: Double): ExponentialDecayScoreDefinition = copy(decay = decay.some)
  def offset(offset: Any): ExponentialDecayScoreDefinition = copy(offset = offset.some)
}
