package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class RegexQueryDefinition(field: String,
                                regex: String,
                                flags: Seq[String] = Nil,
                                boost: Option[Double] = None,
                                queryName: Option[String] = None,
                                rewrite: Option[String] = None)
  extends MultiTermQueryDefinition {

  def queryName(queryName: String): RegexQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): RegexQueryDefinition = copy(boost = boost.some)
  def rewrite(rewrite: String): RegexQueryDefinition = copy(rewrite = rewrite.some)
  def flags(flags: String*): RegexQueryDefinition = copy(flags = flags)
  def flags(flags: Iterable[String]): RegexQueryDefinition = copy(flags = flags.toSeq)
}

object RegexpFlag {
  val INTERSECTION = "INTERSECTION"
  val COMPLEMENT = "COMPLEMENT"
  val EMPTY = "EMPTY"
  val ANYSTRING = "ANYSTRING"
  val INTERVAL = "INTERVAL"
  val NONE = "NONE"
  val ALL = "ALL"
}
