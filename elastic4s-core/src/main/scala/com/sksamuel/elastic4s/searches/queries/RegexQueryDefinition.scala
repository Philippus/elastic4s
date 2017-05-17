package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

sealed trait RegexpFlag
object RegexpFlag {
  case object Intersection extends RegexpFlag
  case object Complement extends RegexpFlag
  case object Empty extends RegexpFlag
  case object AnyString extends RegexpFlag
  case object Interval extends RegexpFlag
  case object None extends RegexpFlag
  case object All extends RegexpFlag
}

case class RegexQueryDefinition(field: String,
                                regex: String,
                                flags: Seq[RegexpFlag] = Nil,
                                boost: Option[Double] = None,
                                maxDeterminedStates: Option[Int] = None,
                                queryName: Option[String] = None,
                                rewrite: Option[String] = None)
  extends MultiTermQueryDefinition {

  def maxDeterminedStates(max: Int): RegexQueryDefinition = copy(maxDeterminedStates = max.some)
  def queryName(queryName: String): RegexQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): RegexQueryDefinition = copy(boost = boost.some)
  def rewrite(rewrite: String): RegexQueryDefinition = copy(rewrite = rewrite.some)
  def flags(flags: RegexpFlag*): RegexQueryDefinition = copy(flags = flags)
  def flags(flags: Iterable[RegexpFlag]): RegexQueryDefinition = copy(flags = flags.toSeq)
}
