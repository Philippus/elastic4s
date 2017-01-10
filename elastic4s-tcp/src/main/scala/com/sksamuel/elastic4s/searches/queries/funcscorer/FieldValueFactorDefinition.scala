package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction
import org.elasticsearch.index.query.functionscore.{FieldValueFactorFunctionBuilder, ScoreFunctionBuilders}
import com.sksamuel.exts.OptionImplicits._

case class FieldValueFactorDefinition(fieldName: String,
                                      factor: Option[Double] = None,
                                      modifier: Option[FieldValueFactorFunction.Modifier] = None,
                                      missing: Option[Double] = None)
  extends ScoreFunctionDefinition {

  override type B = FieldValueFactorFunctionBuilder

  override def builder = {
    val builder = ScoreFunctionBuilders.fieldValueFactorFunction(fieldName)
    factor.map(_.toFloat).foreach(builder.factor)
    missing.foreach(builder.missing)
    modifier.foreach(builder.modifier)
    builder
  }

  def factor(factor: Double): FieldValueFactorDefinition = copy(factor = factor.some)
  def missing(missing: Double): FieldValueFactorDefinition = copy(missing = missing.some)
  def modifier(modifier: FieldValueFactorFunction.Modifier): FieldValueFactorDefinition = copy(modifier = modifier.some)
}
