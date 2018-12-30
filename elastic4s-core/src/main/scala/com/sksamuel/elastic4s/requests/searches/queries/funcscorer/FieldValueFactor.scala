package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class FieldValueFactor(fieldName: String,
                            factor: Option[Double] = None,
                            modifier: Option[FieldValueFactorFunctionModifier] = None,
                            missing: Option[Double] = None,
                            override val filter: Option[Query] = None)
    extends ScoreFunction {
  def factor(factor: Double): FieldValueFactor                               = copy(factor = factor.some)
  def missing(missing: Double): FieldValueFactor                             = copy(missing = missing.some)
  def modifier(modifier: FieldValueFactorFunctionModifier): FieldValueFactor = copy(modifier = modifier.some)
  def filter(filter: Query): FieldValueFactor                                = copy(filter = filter.some)

}

sealed trait FieldValueFactorFunctionModifier
object FieldValueFactorFunctionModifier {
  case object NONE       extends FieldValueFactorFunctionModifier
  case object LOG        extends FieldValueFactorFunctionModifier
  case object LOG1P      extends FieldValueFactorFunctionModifier
  case object LOG2P      extends FieldValueFactorFunctionModifier
  case object LN         extends FieldValueFactorFunctionModifier
  case object LN1P       extends FieldValueFactorFunctionModifier
  case object LN2P       extends FieldValueFactorFunctionModifier
  case object SQUARE     extends FieldValueFactorFunctionModifier
  case object SQRT       extends FieldValueFactorFunctionModifier
  case object RECIPROCAL extends FieldValueFactorFunctionModifier
}
