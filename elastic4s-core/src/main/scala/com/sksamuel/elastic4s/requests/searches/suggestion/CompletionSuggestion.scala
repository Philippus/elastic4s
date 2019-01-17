package com.sksamuel.elastic4s.requests.searches.suggestion

import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.RegexpFlag
import com.sksamuel.exts.OptionImplicits._

sealed trait Fuzziness
object Fuzziness {

  def fromEdits(edits: Int): Fuzziness = edits match {
    case 0 => Zero
    case 1 => One
    case 2 => Two
  }

  case object Zero extends Fuzziness
  case object One  extends Fuzziness
  case object Two  extends Fuzziness
  case object Auto extends Fuzziness
}

case class CompletionSuggestion(name: String,
                                fieldname: String,
                                analyzer: Option[String] = None,
                                fuzziness: Option[Fuzziness] = None,
                                fuzzyMinLength: Option[Int] = None,
                                prefix: Option[String] = None,
                                fuzzyPrefixLength: Option[Int] = None,
                                maxDeterminizedStates: Option[Int] = None,
                                regex: Option[String] = None,
                                regexFlags: Seq[RegexpFlag] = Nil,
                                shardSize: Option[Int] = None,
                                size: Option[Int] = None,
                                transpositions: Option[Boolean] = None,
                                unicodeAware: Option[Boolean] = None,
                                skipDuplicates: Option[Boolean] = None,
                                text: Option[String] = None,
                                contexts: Map[String, Seq[CompletionContext]] = Map.empty)
    extends Suggestion {

  def regex(regex: String): CompletionSuggestion               = copy(regex = regex.some)
  def regexFlags(flags: Seq[RegexpFlag]): CompletionSuggestion = copy(regexFlags = flags)
  def fuzzyMinLength(min: Int): CompletionSuggestion           = copy(fuzzyMinLength = min.some)
  def maxDeterminizedStates(states: Int): CompletionSuggestion = copy(maxDeterminizedStates = states.some)
  def fuzziness(edits: Int): CompletionSuggestion              = copy(fuzziness = Fuzziness.fromEdits(edits).some)
  def fuzziness(fuzziness: Fuzziness): CompletionSuggestion    = copy(fuzziness = fuzziness.some)
  def transpositions(transpositions: Boolean): CompletionSuggestion =
    copy(transpositions = transpositions.some)
  def unicodeAware(unicodeAware: Boolean): CompletionSuggestion = copy(unicodeAware = unicodeAware.some)
  def skipDuplicates(skipDuplicates: Boolean): CompletionSuggestion =
    copy(skipDuplicates = skipDuplicates.some)

  def context(name: String, context: CompletionContext): CompletionSuggestion = contexts(name, Seq(context))
  def contexts(name: String, contexts: Seq[CompletionContext]): CompletionSuggestion =
    copy(contexts = this.contexts + (name -> contexts))

  // adds more contexts to this query
  def contexts(map: Map[String, Seq[CompletionContext]]): CompletionSuggestion =
    copy(contexts = this.contexts ++ map)

  def prefix(prefix: String): CompletionSuggestion = copy(prefix = prefix.some)
  def prefix(prefix: String, fuzziness: Fuzziness): CompletionSuggestion =
    copy(prefix = prefix.some, fuzziness = fuzziness.some)
  def fuzzyPrefixLength(length: Int): CompletionSuggestion = copy(fuzzyPrefixLength = length.some)

  override def analyzer(analyzer: String): CompletionSuggestion = copy(analyzer = analyzer.some)
  override def text(text: String): CompletionSuggestion         = copy(text = text.some)
  override def shardSize(shardSize: Int): CompletionSuggestion  = copy(shardSize = shardSize.some)
  override def size(size: Int): CompletionSuggestion            = copy(size = size.some)

  def isFuzzy: Boolean =
    fuzziness.isDefined ||
      fuzzyMinLength.isDefined ||
      fuzzyPrefixLength.isDefined ||
      transpositions.isDefined ||
      unicodeAware.isDefined
}

sealed trait CompletionContext
case class CategoryContext(name: String, boost: Double = 1, prefix: Boolean = false) extends CompletionContext
case class GeoContext(geoPoint: GeoPoint, precision: String, boost: Double = 1) extends CompletionContext
