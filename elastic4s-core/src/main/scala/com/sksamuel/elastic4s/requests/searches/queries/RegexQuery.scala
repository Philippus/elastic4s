package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits._

sealed trait RegexpFlag
object RegexpFlag {
  case object Intersection extends RegexpFlag
  case object Complement   extends RegexpFlag
  case object Empty        extends RegexpFlag
  case object AnyString    extends RegexpFlag
  case object Interval     extends RegexpFlag
  case object None         extends RegexpFlag
  case object All          extends RegexpFlag
}

case class RegexQuery(field: String,
                      regex: String,
                      flags: Seq[RegexpFlag] = Nil,
                      boost: Option[Double] = None,
                      maxDeterminedStates: Option[Int] = None,
                      queryName: Option[String] = None,
                      rewrite: Option[String] = None)
    extends MultiTermQuery {

  def maxDeterminedStates(max: Int): RegexQuery      = copy(maxDeterminedStates = max.some)
  def queryName(queryName: String): RegexQuery       = copy(queryName = queryName.some)
  def boost(boost: Double): RegexQuery               = copy(boost = boost.some)
  def rewrite(rewrite: String): RegexQuery           = copy(rewrite = rewrite.some)
  def flags(flags: RegexpFlag*): RegexQuery          = copy(flags = flags)
  def flags(flags: Iterable[RegexpFlag]): RegexQuery = copy(flags = flags.toSeq)
}
