package com.sksamuel.elastic4s.http.search.suggs

import com.sksamuel.elastic4s.http.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.suggestion.CompletionSuggestion

object CompletionSuggestionBuilderFn {

  def apply(completion: CompletionSuggestion): XContentBuilder = {

    val builder = XContentFactory.obj()

    completion.text.foreach(builder.field("text", _))
    completion.prefix.foreach(builder.field("prefix", _))

    completion.regex.foreach(builder.field("regex", _))

    builder.startObject("completion")

    builder.field("field", completion.fieldname)

    completion.analyzer.foreach(builder.field("analyzer", _))
    completion.size.foreach(builder.field("size", _))
    completion.shardSize.foreach(builder.field("shard_size", _))
    completion.skipDuplicates.foreach(builder.field("skip_duplicates", _))

    completion.regex.foreach { regex =>
      builder.startObject("regex")
      completion.maxDeterminizedStates.foreach(builder.field("max_determinized_states", _))
      if (completion.regexFlags.nonEmpty)
        builder.field("flags", completion.regexFlags.map(EnumConversions.regexpFlag).mkString("|"))
      builder.endObject()
    }

    if (completion.isFuzzy) {
      builder.startObject("fuzzy")
      completion.fuzziness.map(EnumConversions.fuzziness).foreach(builder.field("fuzziness", _))
      completion.fuzzyMinLength.foreach(builder.field("min_length", _))
      completion.fuzzyPrefixLength.foreach(builder.field("prefix_length", _))
      completion.transpositions.foreach(builder.field("transpositions", _))
      completion.unicodeAware.foreach(builder.field("unicode_aware", _))
      builder.endObject()
    }

    if (completion.contexts.nonEmpty) {
      builder.startObject("contexts")
      completion.contexts.foreach {
        case (key, value) =>
          builder.startArray(key)
          value.foreach { context =>
            builder.startObject()
            builder.field("context", context.name)
            builder.field("boost", context.boost)
            builder.field("prefix", context.prefix)
            builder.endObject()
          }
          builder.endArray()
      }
      builder.endObject()
    }

    builder
  }
}
