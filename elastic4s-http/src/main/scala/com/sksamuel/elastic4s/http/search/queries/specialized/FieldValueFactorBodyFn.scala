package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.searches.queries.funcscorer.FieldValueFactorDefinition
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction.Modifier
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object FieldValueFactorBodyFn {
  def apply(f: FieldValueFactorDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()

    builder.field("field", f.fieldName)
    f.factor.map(f => builder.field("factor", f))
    f.missing.map(mis => builder.field("missing", mis))
    builder.field("modifier", f.modifier.getOrElse(Modifier.NONE).toString.toLowerCase)

    builder.endObject()
    builder
  }
}
