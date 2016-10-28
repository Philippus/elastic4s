package com.sksamuel.elastic4s2.search.queries.funcscorer

import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction
import org.elasticsearch.index.query.functionscore.FieldValueFactorFunctionBuilder

case class FieldValueFactorDefinition(fieldName: String) extends ScoreDefinition[FieldValueFactorDefinition] {

  override val builder = new FieldValueFactorFunctionBuilder(fieldName: String)

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
