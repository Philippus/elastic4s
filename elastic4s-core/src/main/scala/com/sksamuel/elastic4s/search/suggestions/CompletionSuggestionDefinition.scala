package com.sksamuel.elastic4s.search.suggestions

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.search.suggest.SuggestBuilders
import org.elasticsearch.search.suggest.completion.{CompletionSuggestionBuilder, RegexOptions}

case class CompletionSuggestionDefinition(fieldname: String,
                                          prefix: Option[String] = None,
                                          regex: Option[String] = None,
                                          regexOptions: Option[RegexOptions] = None,
                                          analyzer: Option[String] = None,
                                          size: Option[Int] = None,
                                          fuzziness: Option[Fuzziness] = None,
                                          shardSize: Option[Int] = None,
                                          text: Option[String] = None) extends SuggestionDefinition {

  override type B = CompletionSuggestionBuilder

  override def builder = {
    val builder = SuggestBuilders.completionSuggestion(fieldname)
    super.populate(builder)

    prefix.foreach { prefix =>
      builder.prefix(prefix, fuzziness.orNull)
    }

    regex.foreach { regex =>
      builder.regex(regex, regexOptions.orNull)
    }

    builder
  }

  def prefix(prefix: String): CompletionSuggestionDefinition = copy(prefix = prefix.some)
  def prefix(prefix: String, fuzziness: Fuzziness): CompletionSuggestionDefinition =
    copy(prefix = prefix.some, fuzziness = fuzziness.some)

  override def analyzer(analyzer: String): CompletionSuggestionDefinition = copy(analyzer = analyzer.some)
  override def text(text: String): CompletionSuggestionDefinition = copy(text = text.some)
  override def size(size: Int): CompletionSuggestionDefinition = copy(size = size.some)
  override def shardSize(shardSize: Int): CompletionSuggestionDefinition = copy(shardSize = shardSize.some)
}
