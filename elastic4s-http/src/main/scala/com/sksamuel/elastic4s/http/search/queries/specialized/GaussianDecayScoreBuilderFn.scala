package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.{EnumConversions, ScriptBuilderFn}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.funcscorer._

object GaussianDecayScoreBuilderFn {
  def apply(g: GaussianDecayScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("gauss")
    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(_.toString).foreach(builder.field("offset", _))
    g.decay.foreach(builder.field("decay", _))
    builder.endObject()
    g.multiValueMode.map(EnumConversions.multiValueMode).foreach(builder.field("multi_value_mode", _))
    builder.endObject()
    g.weight.foreach(builder.field("weight", _))
    builder
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
    s.weight.foreach(builder.field("weight", _))
    builder
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

object ExponentialDecayScoreBuilderFn {
  def apply(g: ExponentialDecayScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("exp")
    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(_.toString).foreach(builder.field("offset", _))
    g.decay.foreach(builder.field("decay", _))
    builder.endObject()
    g.multiValueMode.map(EnumConversions.multiValueMode).foreach(builder.field("multi_value_mode", _))
    builder.endObject()
    g.weight.foreach(builder.field("weight", _))
    builder
  }
}

object LinearDecayScoreBuilderFn {
  def apply(g: LinearDecayScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("linear")
    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(_.toString).foreach(builder.field("offset", _))
    g.decay.foreach(builder.field("decay", _))
    builder.endObject()
    g.multiValueMode.map(EnumConversions.multiValueMode).foreach(builder.field("multi_value_mode", _))
    builder.endObject()
    g.weight.foreach(builder.field("weight", _))
    builder
  }
}

object WeightBuilderFn {
  def apply(w: WeightScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("weight", w.weight.toFloat)
  }
}






