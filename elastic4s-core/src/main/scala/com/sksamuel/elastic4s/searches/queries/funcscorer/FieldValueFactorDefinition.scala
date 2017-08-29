package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class FieldValueFactorDefinition(fieldName: String,
                                      factor: Option[Double] = None,
                                      modifier: Option[FieldValueFactorFunctionModifier] = None,
                                      missing: Option[Double] = None,
                                      override val filter: Option[QueryDefinition] = None) extends ScoreFunctionDefinition {
  def factor(factor: Double): FieldValueFactorDefinition = copy(factor = factor.some)
  def missing(missing: Double): FieldValueFactorDefinition = copy(missing = missing.some)
  def modifier(modifier: FieldValueFactorFunctionModifier): FieldValueFactorDefinition = copy(modifier = modifier.some)
  def filter(filter: QueryDefinition): FieldValueFactorDefinition = copy(filter = filter.some)

}

sealed trait FieldValueFactorFunctionModifier
object FieldValueFactorFunctionModifier {
  case object NONE extends FieldValueFactorFunctionModifier
  case object LOG extends FieldValueFactorFunctionModifier
  case object LOG1P extends FieldValueFactorFunctionModifier
  case object LOG2P extends FieldValueFactorFunctionModifier
  case object LN extends FieldValueFactorFunctionModifier
  case object LN1P extends FieldValueFactorFunctionModifier
  case object LN2P extends FieldValueFactorFunctionModifier
  case object SQUARE extends FieldValueFactorFunctionModifier
  case object SQRT extends FieldValueFactorFunctionModifier
  case object RECIPROCAL extends FieldValueFactorFunctionModifier
}
