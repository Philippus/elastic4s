package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class RegexQuery(field: String,
                      regex: String,
                      flags: Seq[RegexpFlag] = Nil,
                      boost: Option[Double] = None,
                      maxDeterminedStates: Option[Int] = None,
                      queryName: Option[String] = None,
                      rewrite: Option[String] = None)
  extends MultiTermQuery {

  def maxDeterminedStates(max: Int): RegexQuery = copy(maxDeterminedStates = max.some)
  def queryName(queryName: String): RegexQuery = copy(queryName = queryName.some)
  def boost(boost: Double): RegexQuery = copy(boost = boost.some)
  def rewrite(rewrite: String): RegexQuery = copy(rewrite = rewrite.some)
  def flags(flags: RegexpFlag*): RegexQuery = copy(flags = flags)
  def flags(flags: Iterable[RegexpFlag]): RegexQuery = copy(flags = flags.toSeq)
}
