package com.sksamuel.elastic4s.searches.suggestion

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.suggest.completion.RegexOptions

sealed trait Fuzziness
object Fuzziness {

  def fromEdits(edits: Int): Fuzziness = edits match {
    case 0 => Zero
    case 1 => One
    case 2 => Two
  }

  case object Zero extends Fuzziness
  case object One extends Fuzziness
  case object Two extends Fuzziness
  case object Auto extends Fuzziness
}


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
                                          text: Option[String] = None,
                                          contexts: Map[String, Seq[CategoryContext]] = Map.empty) extends SuggestionDefinition {

  def regex(regex: String): CompletionSuggestionDefinition = copy(regex = regex.some)
  def regexOptions(regexOptions: RegexOptions): CompletionSuggestionDefinition = copy(regexOptions = regexOptions.some)
  def fuzzyMinLength(min: Int): CompletionSuggestionDefinition = copy(fuzzyMinLength = min.some)
  def maxDeterminizedStates(states: Int): CompletionSuggestionDefinition = copy(maxDeterminizedStates = states.some)
  def fuzziness(edits: Int): CompletionSuggestionDefinition = copy(fuzziness = Fuzziness.fromEdits(edits).some)
  def fuzziness(fuzziness: Fuzziness): CompletionSuggestionDefinition = copy(fuzziness = fuzziness.some)
  def transpositions(transpositions: Boolean): CompletionSuggestionDefinition = copy(transpositions = transpositions.some)
  def unicodeAware(unicodeAware: Boolean): CompletionSuggestionDefinition = copy(unicodeAware = unicodeAware.some)

  def context(name: String, context: CategoryContext): CompletionSuggestionDefinition = contexts(name, Seq(context))
  def contexts(name: String, contexts: Seq[CategoryContext]): CompletionSuggestionDefinition = {
    copy(contexts = this.contexts + (name -> contexts))
  }

  // adds more contexts to this query
  def contexts(map: Map[String, Seq[CategoryContext]]): CompletionSuggestionDefinition = copy(contexts = this.contexts ++ map)

  def prefix(prefix: String): CompletionSuggestionDefinition = copy(prefix = prefix.some)
  def prefix(prefix: String, fuzziness: Fuzziness): CompletionSuggestionDefinition =
    copy(prefix = prefix.some, fuzziness = fuzziness.some)
  def fuzzyPrefixLength(length: Int): CompletionSuggestionDefinition = copy(fuzzyPrefixLength = length.some)

  override def analyzer(analyzer: String): CompletionSuggestionDefinition = copy(analyzer = analyzer.some)
  override def text(text: String): CompletionSuggestionDefinition = copy(text = text.some)
  override def shardSize(shardSize: Int): CompletionSuggestionDefinition = copy(shardSize = shardSize.some)
  override def size(size: Int): CompletionSuggestionDefinition = copy(size = size.some)
}

case class CategoryContext(name: String, boost: Double = 0, prefix: Boolean = false)
