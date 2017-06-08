package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.searches.queries.MoreLikeThisQueryDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

import scala.collection.JavaConverters._

object MoreLikeThisBuilderFn {
  def apply(q: MoreLikeThisQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.startObject()
    builder.startObject("more_like_this")
    builder.field("fields", q.fields.asJava)

    builder.startArray("like")
    q.likeTexts.foreach(text => builder.value(text))
    q.likeDocs.foreach { doc =>
      builder.startObject()
      builder.field("_index", doc.index)
      builder.field("_type", doc.`type`)
      builder.field("_id", doc.id)
      doc.routing.foreach { r ⇒ builder.field("_routing", r) }
      builder.endObject()
    }
    q.artificialDocs.foreach { doc =>
      builder.startObject()
      builder.field("_index", doc.index)
      builder.field("_type", doc.`type`)
      builder.rawField("doc", new BytesArray(doc.doc))
      doc.routing.foreach { r ⇒ builder.field("_routing", r) }
      builder.endObject()
    }
    builder.endArray()

    if (q.unlikeTexts.nonEmpty || q.unlikeDocs.nonEmpty) {
      builder.startArray("unlike")
      q.unlikeTexts.foreach(text => builder.value(text))
      q.unlikeDocs.foreach { doc =>
        builder.startObject()
        builder.field("_index", doc.index)
        builder.field("_type", doc.`type`)
        builder.field("_id", doc.id)
        doc.routing.foreach { r ⇒ builder.field("_routing", r) }
        builder.endObject()
      }
      builder.endArray()
    }

    q.minTermFreq.foreach(builder.field("min_term_freq", _))
    q.maxQueryTerms.foreach(builder.field("max_query_terms", _))
    q.minDocFreq.foreach(builder.field("min_doc_freq", _))
    q.maxDocFreq.foreach(builder.field("max_doc_freq", _))
    q.minWordLength.foreach(builder.field("min_word_length", _))
    q.maxWordLength.foreach(builder.field("max_word_length", _))

    if (q.stopWords.nonEmpty)
      builder.field("stop_words", q.stopWords.asJava)

    q.analyzer.foreach(builder.field("analyzer", _))
    q.minShouldMatch.foreach(builder.field("minimum_should_match", _))
    q.boostTerms.foreach(builder.field("boost_terms", _))
    q.include.foreach(builder.field("include", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
    builder
  }
}
