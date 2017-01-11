package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class RangeQueryDefinition(field: String,
                                boost: Option[Double] = None,
                                timeZone: Option[String] = None,
                                includeUpper: Option[Boolean] = None,
                                includeLower: Option[Boolean] = None,
                                lte: Option[String] = None,
                                gte: Option[String] = None,
                                from: Option[Any] = None,
                                to: Option[Any] = None,
                                queryName: Option[String] = None)
  extends MultiTermQueryDefinition {

  def boost(boost: Double): RangeQueryDefinition = copy(boost = boost.some)
  def from(f: Any): RangeQueryDefinition = copy(from = f.some)
  def to(to: Any): RangeQueryDefinition = copy(to = to.some)
  def gte(gte: String): RangeQueryDefinition = copy(gte = gte.some)
  def gte(gte: Double): RangeQueryDefinition = copy(gte = gte.toString.some)
  def lte(lte: String): RangeQueryDefinition = copy(lte = lte.some)
  def lte(lte: Double): RangeQueryDefinition = copy(lte = lte.toString.some)
  def includeUpper(includeUpper: Boolean): RangeQueryDefinition = copy(includeUpper = includeUpper.some)
  def includeLower(includeLower: Boolean): RangeQueryDefinition = copy(includeLower = includeLower.some)
  def timeZone(timeZone: String): RangeQueryDefinition = copy(timeZone = timeZone.some)
  def queryName(queryName: String): RangeQueryDefinition = copy(queryName = queryName.some)
}
