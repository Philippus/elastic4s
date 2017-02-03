package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.index.query.RegexpFlag

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
