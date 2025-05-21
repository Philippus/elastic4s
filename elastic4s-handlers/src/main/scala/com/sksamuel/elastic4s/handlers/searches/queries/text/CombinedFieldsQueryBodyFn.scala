package com.sksamuel.elastic4s.handlers.searches.queries.text

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.CombinedFieldsQuery

object CombinedFieldsQueryBodyFn {
  def apply(q: CombinedFieldsQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("combined_fields")
    builder.field("query", q.query)

    if (q.fields.nonEmpty) {
      val fields = q.fields.map {
        case (name, boost) => boost.fold(name)(boost => s"$name^$boost")
      }.toArray
      builder.array("fields", fields)
    }

    q.autoGenerateSynonymsPhraseQuery.foreach(builder.field("auto_generate_synonyms_phrase_query", _))
    q.operator.map(_.toString).foreach(builder.field("operator", _))

    q.minimumShouldMatch.foreach(builder.field("minimum_should_match", _))
    q.zeroTermsQuery.map(EnumConversions.zeroTermsQuery).foreach(builder.field("zero_terms_query", _))
    builder.endObject().endObject()
  }
}
