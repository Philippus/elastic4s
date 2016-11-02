package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction
import org.elasticsearch.index.query.functionscore.{FieldValueFactorFunctionBuilder, ScoreFunctionBuilders}

case class FieldValueFactorDefinition(fieldName: String)
  extends ScoreFunctionDefinition {

  override type B = FieldValueFactorFunctionBuilder

  override def builder = ScoreFunctionBuilders.fieldValueFactorFunction(fieldName)

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
