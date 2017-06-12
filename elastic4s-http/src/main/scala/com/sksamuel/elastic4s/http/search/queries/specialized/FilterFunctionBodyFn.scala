package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.funcscorer._
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object FilterFunctionBodyFn {
  def apply(func: FilterFunctionDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    func.filter.map(q => builder.rawField("filter", QueryBuilderFn(q).bytes()))

    func.score match {
      case exp: ExponentialDecayScoreDefinition =>
        builder.rawField("exp", ExponentialDecayScoreBodyFn(exp).bytes())
        exp.weight.foreach(w => builder.field("weight", w.toFloat))
      case f: FieldValueFactorDefinition =>
        builder.rawField("field_value_factor", FieldValueFactorBodyFn(f).bytes())
      case g: GaussianDecayScoreDefinition =>
        builder.rawField("gaussian", GaussianDecayScoreBodyFn(g).bytes())
        g.weight.foreach(w => builder.field("weight", w.toFloat))
      case random: RandomScoreFunctionDefinition =>
        builder.rawField("random_score", RandomScoreFunctionBodyFn(random).bytes())
        random.weight.foreach(w => builder.field("weight", w.toFloat))
      case linear: LinearDecayScoreDefinition =>
        builder.rawField("linear", LinearDecayScoreBodyFn(linear).bytes())
        linear.weight.foreach(w => builder.field("weight", w.toFloat))
      case script: ScriptScoreDefinition =>
        builder.rawField("script_score", ScriptScoreBodyFn(script).bytes())
        script.weight.foreach(w => builder.field("weight", w.toFloat))
      case WeightScoreDefinition(weight) =>
        builder.field("weight", weight.toFloat)
    }

    builder.endObject()
    builder
  }
}
