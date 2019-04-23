package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer._
import com.sksamuel.elastic4s.{
  EnumConversions,
  XContentBuilder,
  XContentFactory
}

object GaussianDecayScoreBuilderFn {
  def apply(g: GaussianDecayScore): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("gauss")
    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(_.toString).foreach(builder.field("offset", _))
    g.decay.foreach(builder.field("decay", _))
    builder.endObject()
    g.multiValueMode
      .map(EnumConversions.multiValueMode)
      .foreach(builder.field("multi_value_mode", _))
    builder.endObject()
    g.filter.foreach(filter =>
      builder.rawField("filter", QueryBuilderFn.apply(filter)))
    g.weight.foreach(builder.field("weight", _))
    builder
  }
}

object RandomScoreFunctionBuilderFn {
  def apply(r: RandomScoreFunction): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("random_score")
    builder.field("seed", r.seed)
    builder.field("field", r.fieldName)
    builder.endObject()
    r.weight.foreach(builder.field("weight", _))
    r.filter.foreach(filter =>
      builder.rawField("filter", QueryBuilderFn.apply(filter)))
    builder
  }
}

object ScriptScoreBuilderFn {
  def apply(s: ScriptScore): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("script_score")
    builder.rawField("script", ScriptBuilderFn(s.script))
    builder.endObject()
    s.weight.foreach(builder.field("weight", _))
    s.filter.foreach(filter =>
      builder.rawField("filter", QueryBuilderFn.apply(filter)))
    builder
  }
}

object FieldValueFactorBuilderFn {
  def apply(f: FieldValueFactor): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("field_value_factor")
    builder.field("field", f.fieldName)
    f.factor.foreach(builder.field("factor", _))
    f.modifier.map(_.toString.toLowerCase).foreach(builder.field("modifier", _))
    f.missing.foreach(builder.field("missing", _))
    builder.endObject()
    f.filter.foreach(filter =>
      builder.rawField("filter", QueryBuilderFn.apply(filter)))
    builder
  }
}

object ExponentialDecayScoreBuilderFn {
  def apply(g: ExponentialDecayScore): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("exp")
    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(_.toString).foreach(builder.field("offset", _))
    g.decay.foreach(builder.field("decay", _))
    builder.endObject()
    g.multiValueMode
      .map(EnumConversions.multiValueMode)
      .foreach(builder.field("multi_value_mode", _))
    builder.endObject()
    g.weight.foreach(builder.field("weight", _))
    g.filter.foreach(filter =>
      builder.rawField("filter", QueryBuilderFn.apply(filter)))
    builder
  }
}

object LinearDecayScoreBuilderFn {
  def apply(g: LinearDecayScore): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("linear")
    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(_.toString).foreach(builder.field("offset", _))
    g.decay.foreach(builder.field("decay", _))
    builder.endObject()
    g.multiValueMode
      .map(EnumConversions.multiValueMode)
      .foreach(builder.field("multi_value_mode", _))
    builder.endObject()
    g.weight.foreach(builder.field("weight", _))
    g.filter.foreach(filter =>
      builder.rawField("filter", QueryBuilderFn.apply(filter)))
    builder
  }
}

object WeightBuilderFn {
  def apply(w: WeightScore): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("weight", w.weight.toFloat)
    w.filter.foreach(filter =>
      builder.rawField("filter", QueryBuilderFn.apply(filter)))
    builder
  }
}
