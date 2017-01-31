package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction

case class FieldValueFactorDefinition(fieldName: String,
                                      factor: Option[Double] = None,
                                      modifier: Option[FieldValueFactorFunction.Modifier] = None,
                                      missing: Option[Double] = None) extends ScoreFunctionDefinition {
  def factor(factor: Double): FieldValueFactorDefinition = copy(factor = factor.some)
  def missing(missing: Double): FieldValueFactorDefinition = copy(missing = missing.some)
  def modifier(modifier: FieldValueFactorFunction.Modifier): FieldValueFactorDefinition = copy(modifier = modifier.some)
}
