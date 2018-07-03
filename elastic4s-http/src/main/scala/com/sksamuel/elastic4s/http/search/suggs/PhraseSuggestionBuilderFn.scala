package com.sksamuel.elastic4s.http.search.suggs

import com.sksamuel.elastic4s.http.{EnumConversions, SourceAsContentBuilder}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.suggestion.PhraseSuggestion

object PhraseSuggestionBuilderFn {
  def apply(phrase: PhraseSuggestion): XContentBuilder = {

    val builder = XContentFactory.obj()

    phrase.text.foreach(builder.field("text", _))
    builder.startObject("phrase")

    builder.field("field", phrase.fieldname)
    phrase.analyzer.foreach(builder.field("analyzer", _))

    phrase.confidence.foreach(builder.field("confidence", _))
    phrase.forceUnigrams.foreach(builder.field("force_unigrams", _))
    phrase.gramSize.foreach(builder.field("gram_size", _))
    phrase.maxErrors.foreach(builder.field("max_errors", _))
    phrase.realWordErrorLikelihood.foreach(builder.field("real_word_error_likelihood", _))
    phrase.separator.foreach(builder.field("separator", _))
    phrase.tokenLimit.foreach(builder.field("token_limit", _))
    phrase.size.foreach(builder.field("size", _))
    phrase.shardSize.foreach(builder.field("shard_size", _))

    //COLLATE
    builder.startObject("collate")

    builder.startObject("query")
    phrase.collateQuery.foreach(t => builder.rawField("inline", t.script))
    builder.endObject()

    phrase.collatePrune.foreach(builder.field("prune", _))
    builder.rawField("params", SourceAsContentBuilder(phrase.collateParams))

    builder.endObject()
    //END COLLATE

    //highlight
    builder.startObject("highlight")
    phrase.preTag.foreach(builder.field("pre_tag", _))
    phrase.postTag.foreach(builder.field("post_tag", _))
    builder.endObject()
    //end

    builder
  }
}
