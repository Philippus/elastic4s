package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.funcscorer.{FieldValueFactorDefinition, GaussianDecayScoreDefinition, RandomScoreFunctionDefinition, ScriptScoreDefinition}

object GaussianDecayScoreBuilderFn {
  def apply(g: GaussianDecayScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("gaussian")
    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(_.toString).foreach(builder.field("offset", _))
    g.decay.foreach(builder.field("decay", _))
    g.weight.foreach(builder.field("weight", _))
    builder.endObject().endObject()
  }
}

object RandomScoreFunctionBuilderFn {
  def apply(r: RandomScoreFunctionDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("random_score")
    builder.field("seed", r.seed)
    builder.endObject()
  }
}

object ScriptScoreBuilderFn {
  def apply(s: ScriptScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("script_score")
    builder.rawField("script", ScriptBuilderFn(s.script))
    builder.endObject()
  }
}

object FieldValueFactorBuilderFn {
  def apply(f: FieldValueFactorDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("field_value_factor")
    builder.field("field", f.fieldName)
    f.factor.foreach(builder.field("factor", _))
    f.modifier.map(_.toString.toLowerCase).foreach(builder.field("modifier", _))
    f.missing.foreach(builder.field("missing", _))
    builder.endObject()
  }
}
