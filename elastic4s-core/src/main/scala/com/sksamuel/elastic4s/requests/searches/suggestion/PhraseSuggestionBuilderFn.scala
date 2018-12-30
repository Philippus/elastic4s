package com.sksamuel.elastic4s.requests.searches.suggestion

import com.sksamuel.elastic4s.{SourceAsContentBuilder, XContentBuilder, XContentFactory}

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

    // DIRECT GENERATOR
    builder.startArray("direct_generator")
    phrase.directGenerators.foreach { generator =>
      builder.startObject()
      builder.field("field", generator.field)
      generator.size.foreach(builder.field("size", _))
      generator.suggestMode.foreach(builder.field("suggest_mode", _))
      generator.maxEdits.foreach(builder.field("max_edits", _))
      generator.prefixLength.foreach(builder.field("prefix_length", _))
      generator.minWordLength.foreach(builder.field("min_word_length", _))
      generator.maxInspections.foreach(builder.field("max_inspections", _))
      generator.minDocFreq.foreach(builder.field("min_doc_freq", _))
      generator.maxTermFreq.foreach(builder.field("max_term_freq", _))
      generator.preFilter.foreach(builder.field("pre_filter", _))
      generator.postFilter.foreach(builder.field("post_filter", _))
      builder.endObject()
    }
    builder.endArray()
    // END DIRECT GENERATOR

    phrase.collateQuery match {
      case None =>
      case Some(query) =>
        //COLLATE
        builder.startObject("collate")

        builder.startObject("query")
        phrase.collateQuery.foreach(t => builder.rawField(t.scriptType.toString.toLowerCase, t.script))
        builder.endObject()

        phrase.collatePrune.foreach(builder.field("prune", _))
        builder.rawField("params", SourceAsContentBuilder(phrase.collateParams))

        builder.endObject()
        //END COLLATE
    }

    //highlight
    builder.startObject("highlight")
    phrase.preTag.foreach(builder.field("pre_tag", _))
    phrase.postTag.foreach(builder.field("post_tag", _))
    builder.endObject()
    //end

    builder
  }
}
