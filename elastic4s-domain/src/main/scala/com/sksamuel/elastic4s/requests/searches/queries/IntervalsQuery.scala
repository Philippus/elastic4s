package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class IntervalsQuery(field: String, rule: IntervalsRule) extends Query

sealed trait IntervalsRule
case class Match(query: String,
                 maxGaps: Option[Int] = None,
                 ordered: Option[Boolean] = None,
                 analyzer: Option[String] = None,
                 filter: Option[IntervalsFilter] = None,
                 useField: Option[String] = None) extends IntervalsRule {
  override def toString = "match"

  def maxGaps(maxGaps: Int): Match = copy(maxGaps = maxGaps.some)
  def ordered(ordered: Boolean): Match = copy(ordered = ordered.some)
  def analyzer(analyzer: String): Match = copy(analyzer = analyzer.some)
  def filter(filter: IntervalsFilter): Match = copy(filter = filter.some)
  def useField(useField: String): Match = copy(useField = useField.some)
}

case class Prefix(prefix: String,
                  analyzer: Option[String] = None,
                  useField: Option[String] = None) extends IntervalsRule {
  override def toString = "prefix"

  def analyzer(analyzer: String): Prefix = copy(analyzer = analyzer.some)
  def useField(useField: String): Prefix = copy(useField = useField.some)
}

case class Wildcard(pattern: String,
                    analyzer: Option[String] = None,
                    useField: Option[String] = None) extends IntervalsRule {
  override def toString = "wildcard"

  def analyzer(analyzer: String): Wildcard = copy(analyzer = analyzer.some)
  def useField(useField: String): Wildcard = copy(useField = useField.some)
}

case class Fuzzy(term: String,
                 prefixLength: Option[String] = None,
                 transpositions: Option[Boolean] = None,
                 fuzziness: Option[String] = None,
                 analyzer: Option[String] = None,
                 useField: Option[String] = None) extends IntervalsRule {
  override def toString = "fuzzy"

  def prefixLength(prefixLength: String): Fuzzy = copy(prefixLength = prefixLength.some) // maybe Int ?
  def transpositions(transpositions: Boolean): Fuzzy = copy(transpositions = transpositions.some)
  def fuzziness(fuzziness: String): Fuzzy = copy(fuzziness = fuzziness.some)
  def analyzer(analyzer: String): Fuzzy = copy(analyzer = analyzer.some)
  def useField(useField: String): Fuzzy = copy(useField = useField.some)
}

case class AllOf(intervals: List[IntervalsRule],
                 maxGaps: Option[Int] = None,
                 ordered: Option[Boolean] = None,
                 filter: Option[IntervalsFilter] = None) extends IntervalsRule {
  override def toString = "all_of"

  def maxGaps(maxGaps: Int): AllOf = copy(maxGaps = maxGaps.some)
  def ordered(ordered: Boolean): AllOf = copy(ordered = ordered.some)
  def filter(filter: IntervalsFilter): AllOf = copy(filter = filter.some)
}
case class AnyOf(intervals: List[IntervalsRule],
                 filter: Option[IntervalsFilter] = None) extends IntervalsRule {
  override def toString = "any_of"

  def filter(filter: IntervalsFilter): AnyOf = copy(filter = filter.some)
}

case class IntervalsFilter(after: Option[IntervalsRule] = None,
                           before: Option[IntervalsRule] = None,
                           containedBy: Option[IntervalsRule] = None,
                           containing: Option[IntervalsRule] = None,
                           notContainedBy: Option[IntervalsRule] = None,
                           notContaining: Option[IntervalsRule] = None,
                           notOverlapping: Option[IntervalsRule] = None,
                           overlapping: Option[IntervalsRule] = None,
                           script: Option[Script] = None) {
  def after(rule: IntervalsRule): IntervalsFilter = copy(after = rule.some)
  def before(rule: IntervalsRule): IntervalsFilter = copy(before = rule.some)
  def containedBy(rule: IntervalsRule): IntervalsFilter = copy(containedBy = rule.some)
  def containing(rule: IntervalsRule): IntervalsFilter = copy(containing = rule.some)
  def notContainedBy(rule: IntervalsRule): IntervalsFilter = copy(notContainedBy = rule.some)
  def notContaining(rule: IntervalsRule): IntervalsFilter = copy(notContaining = rule.some)
  def notOverlapping(rule: IntervalsRule): IntervalsFilter = copy(notOverlapping = rule.some)
  def overlapping(rule: IntervalsRule): IntervalsFilter = copy(overlapping = rule.some)
  def script(script: Script): IntervalsFilter = copy(script = script.some)
}
