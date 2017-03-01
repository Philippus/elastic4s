package com.sksamuel.elastic4s.searches.suggestion

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.search.suggest.SuggestBuilders
import org.elasticsearch.search.suggest.completion.{CompletionSuggestionBuilder, FuzzyOptions, RegexOptions}

case class CompletionSuggestionDefinition(name: String,
                                          fieldname: String,
                                          analyzer: Option[String] = None,
                                          fuzziness: Option[Fuzziness] = None,
                                          fuzzyMinLength: Option[Int] = None,
                                          prefix: Option[String] = None,
                                          fuzzyPrefixLength: Option[Int] = None,
                                          maxDeterminizedStates: Option[Int] = None,
                                          regex: Option[String] = None,
                                          regexOptions: Option[RegexOptions] = None,
                                          shardSize: Option[Int] = None,
                                          size: Option[Int] = None,
                                          transpositions: Option[Boolean] = None,
                                          unicodeAware: Option[Boolean] = None,
                                          text: Option[String] = None) extends SuggestionDefinition {

  override type B = CompletionSuggestionBuilder

  override def builder: CompletionSuggestionBuilder = {
    val builder = SuggestBuilders.completionSuggestion(fieldname)
    super.populate(builder)

    prefix.foreach { prefix =>
      fuzziness.fold(builder.prefix(prefix)) { fuzz =>
        val options = new FuzzyOptions.Builder()
        options.setFuzziness(fuzz)
        unicodeAware.foreach(options.setUnicodeAware)
        fuzzyMinLength.foreach(options.setFuzzyMinLength)
        fuzzyPrefixLength.foreach(options.setFuzzyPrefixLength)
        maxDeterminizedStates.foreach(options.setMaxDeterminizedStates)
        transpositions.foreach(options.setTranspositions)
        builder.prefix(prefix, options.build)
      }
    }

    regex.foreach { regex =>
      builder.regex(regex, regexOptions.orNull)
    }

    builder
  }

  def regex(regex: String): CompletionSuggestionDefinition = copy(regex = regex.some)
  def regexOptions(regexOptions: RegexOptions): CompletionSuggestionDefinition = copy(regexOptions = regexOptions.some)
  def fuzzyMinLength(min: Int): CompletionSuggestionDefinition = copy(fuzzyMinLength = min.some)
  def maxDeterminizedStates(states: Int): CompletionSuggestionDefinition = copy(maxDeterminizedStates = states.some)
  def fuzziness(edits: Int): CompletionSuggestionDefinition = copy(fuzziness = Fuzziness.fromEdits(edits).some)
  def fuzziness(fuzziness: Fuzziness): CompletionSuggestionDefinition = copy(fuzziness = fuzziness.some)
  def transpositions(transpositions: Boolean): CompletionSuggestionDefinition = copy(transpositions = transpositions.some)
  def unicodeAware(unicodeAware: Boolean): CompletionSuggestionDefinition = copy(unicodeAware = unicodeAware.some)

  def prefix(prefix: String): CompletionSuggestionDefinition = copy(prefix = prefix.some)
  def prefix(prefix: String, fuzziness: Fuzziness): CompletionSuggestionDefinition =
    copy(prefix = prefix.some, fuzziness = fuzziness.some)
  def fuzzyPrefixLength(length: Int): CompletionSuggestionDefinition = copy(fuzzyPrefixLength = length.some)

  override def analyzer(analyzer: String): CompletionSuggestionDefinition = copy(analyzer = analyzer.some)
  override def text(text: String): CompletionSuggestionDefinition = copy(text = text.some)
  override def shardSize(shardSize: Int): CompletionSuggestionDefinition = copy(shardSize = shardSize.some)
  override def size(size: Int): CompletionSuggestionDefinition = copy(size = size.some)
}
