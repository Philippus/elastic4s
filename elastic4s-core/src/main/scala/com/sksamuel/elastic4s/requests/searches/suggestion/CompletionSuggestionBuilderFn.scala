package com.sksamuel.elastic4s.requests.searches.suggestion

import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}

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
      ??? // should use regex here ??
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
            context match {
              case CategoryContext(name, boost, prefix) =>
                builder.field("context", name)
                builder.field("boost", boost)
                builder.field("prefix", prefix)
              case GeoContext(geoPoint, precision, boost) =>
                builder.startObject("context")
                builder.field("lat", geoPoint.lat)
                builder.field("lon", geoPoint.long)
                builder.endObject()
                builder.field("boost", boost)
                builder.field("precision", precision)
            }
            builder.endObject()
          }
          builder.endArray()
      }
      builder.endObject()
    }

    builder
  }
}
