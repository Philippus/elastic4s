package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.{IdQueryDefinition, _}
import com.sksamuel.elastic4s.searches.queries.`match`.{MatchAllQueryDefinition, MatchPhraseDefinition, MatchQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.term.TermQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object QueryBuilderFn {
  def apply(q: QueryDefinition): XContentBuilder = q match {
    case b: BoolQueryDefinition => BoolQueryBuilderFn(b)
    case q: CommonTermsQueryDefinition => CommonTermsQueryBodyFn(q)
    case q: IdQueryDefinition => IdQueryBodyFn(q)
    case q: MatchQueryDefinition => MatchBodyFn(q)
    case q: MatchAllQueryDefinition => MatchAllBodyFn(q)
    case q: MatchPhraseDefinition => MatchPhraseQueryBodyFn(q)
    case q: QueryStringQueryDefinition => QueryStringBodyFn(q)
    case s: SimpleStringQueryDefinition => SimpleStringBodyFn(s)
    case t: TermQueryDefinition => TermQueryBodyFn(t)
  }
}

object IdQueryBodyFn {

  import scala.collection.JavaConverters._

  def apply(q: IdQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("ids")
    if (q.types.nonEmpty) {
      builder.field("type", q.types.asJava)
    }
    builder.field("values", q.ids.asJava)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
