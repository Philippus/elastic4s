package com.sksamuel.elastic4s.searches.suggestions

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.searches.suggestion.{CompletionSuggestionDefinition, Fuzziness}
import org.elasticsearch.common.unit
import org.elasticsearch.common.xcontent.ToXContent
import org.elasticsearch.search.suggest.SuggestBuilders
import org.elasticsearch.search.suggest.completion.context.CategoryQueryContext
import org.elasticsearch.search.suggest.completion.{CompletionSuggestionBuilder, FuzzyOptions, RegexOptions}

import scala.collection.JavaConverters._

object CompletionSuggestionBuilderFn {

  def apply(sugg: CompletionSuggestionDefinition): CompletionSuggestionBuilder = {

    val builder = SuggestBuilders.completionSuggestion(sugg.fieldname)

    sugg.analyzer.foreach(builder.analyzer)
    sugg.shardSize.foreach(builder.shardSize(_))
    sugg.size.foreach(builder.size)
    sugg.text.foreach(builder.text)
    sugg.skipDuplicates.foreach(builder.skipDuplicates)

    sugg.prefix.foreach { prefix =>
      sugg.fuzziness.fold(builder.prefix(prefix)) { fuzz =>
        val options = new FuzzyOptions.Builder()
        options.setFuzziness(fuzz match {
          case Fuzziness.Zero => unit.Fuzziness.ZERO
          case Fuzziness.One  => unit.Fuzziness.ONE
          case Fuzziness.Two  => unit.Fuzziness.TWO
          case Fuzziness.Auto => unit.Fuzziness.AUTO
        })
        sugg.unicodeAware.foreach(options.setUnicodeAware)
        sugg.fuzzyMinLength.foreach(options.setFuzzyMinLength)
        sugg.fuzzyPrefixLength.foreach(options.setFuzzyPrefixLength)
        sugg.maxDeterminizedStates.foreach(options.setMaxDeterminizedStates)
        sugg.transpositions.foreach(options.setTranspositions)
        builder.prefix(prefix, options.build)
      }
    }

    sugg.regex.foreach { regex =>
      val flags = sugg.regexFlags.map(EnumConversions.regexpFlags)
      builder.regex(regex, RegexOptions.builder().setFlags(flags.map(_.name).mkString("|")).build)
    }

    if (sugg.contexts.nonEmpty) {
      val categoryContexts: Map[String, java.util.List[_ <: ToXContent]] = sugg.contexts.map {
        case (name, contexts) =>
          val tocontents = contexts.map { context =>
            CategoryQueryContext.builder
              .setCategory(context.name)
              .setBoost(context.boost.toInt)
              .setPrefix(context.prefix)
              .build
          }.toList: List[_ <: ToXContent]
          name -> tocontents.asJava
      }
      builder.contexts(categoryContexts.asJava)
    }

    builder
  }
}
