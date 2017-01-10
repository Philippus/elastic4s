package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{LinearDecayFunctionBuilder, ScoreFunctionBuilders}
import org.elasticsearch.search.MultiValueMode
import com.sksamuel.exts.OptionImplicits._

case class LinearDecayScoreDefinition(field: String,
                                      origin: String,
                                      scale: String,
                                      offset: Option[Any] = None,
                                      decay: Option[Double] = None,
                                      weight: Option[Double] = None,
                                      multiValueMode: Option[MultiValueMode] = None) extends ScoreFunctionDefinition {
  override type B = LinearDecayFunctionBuilder

  def builder = {
    val builder = (offset, decay) match {
      case (Some(o), Some(d)) => ScoreFunctionBuilders.linearDecayFunction(field, origin, scale, o, d)
      case (Some(o), None) => ScoreFunctionBuilders.linearDecayFunction(field, origin, scale, o)
      case _ => ScoreFunctionBuilders.linearDecayFunction(field, origin, scale)
    }
    weight.map(_.toFloat).foreach(builder.setWeight)
    multiValueMode.foreach(builder.setMultiValueMode)
    builder
  }

  def multiValueMode(multiValueMode: MultiValueMode): LinearDecayScoreDefinition =
    copy(multiValueMode = multiValueMode.some)

  def weight(weight: Double): LinearDecayScoreDefinition = copy(weight = weight.some)
  def decay(decay: Double): LinearDecayScoreDefinition = copy(decay = decay.some)
  def offset(offset: Any): LinearDecayScoreDefinition = copy(offset = offset.some)
}
