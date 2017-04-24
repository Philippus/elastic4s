package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class RangeQueryDefinition(field: String,
                                boost: Option[Double] = None,
                                timeZone: Option[String] = None,
                                includeUpper: Option[Boolean] = None,
                                includeLower: Option[Boolean] = None,
                                lte: Option[Any] = None,
                                gte: Option[Any] = None,
                                gt: Option[Any] = None,
                                lt: Option[Any] = None,
                                format: Option[String] = None,
                                queryName: Option[String] = None)
  extends MultiTermQueryDefinition {

  def boost(boost: Double): RangeQueryDefinition = copy(boost = boost.some)

  def gt(f: Long): RangeQueryDefinition = copy(gt = f.some)
  def lt(to: Long): RangeQueryDefinition = copy(lt = to.some)

  def gt(f: Double): RangeQueryDefinition = copy(gt = f.some)
  def lt(to: Double): RangeQueryDefinition = copy(lt = to.some)

  def gt(f: String): RangeQueryDefinition = copy(gt = f.some)
  def lt(to: String): RangeQueryDefinition = copy(lt = to.some)

  def gte(gte: String): RangeQueryDefinition = copy(gte = gte.some)
  def lte(lte: String): RangeQueryDefinition = copy(lte = lte.some)

  def gte(gte: Double): RangeQueryDefinition = copy(gte = gte.some)
  def lte(lte: Double): RangeQueryDefinition = copy(lte = lte.some)

  def gte(gte: Long): RangeQueryDefinition = copy(gte = gte.some)
  def lte(lte: Long): RangeQueryDefinition = copy(lte = lte.some)

  def format(fmt: String): RangeQueryDefinition = copy(format = fmt.some)

  @deprecated("use lte or lt", "5.3.1")
  def includeUpper(includeUpper: Boolean): RangeQueryDefinition = copy(includeUpper = includeUpper.some)

  @deprecated("use gte or gt", "5.3.1")
  def includeLower(includeLower: Boolean): RangeQueryDefinition = copy(includeLower = includeLower.some)

  @deprecated("use gte or gt", "5.3.1")
  def from(v: Any): RangeQueryDefinition = gt(v.toString)

  @deprecated("use lte or lt", "5.3.1")
  def to(v: Any): RangeQueryDefinition = lt(v.toString)

  def timeZone(timeZone: String): RangeQueryDefinition = copy(timeZone = timeZone.some)
  def queryName(queryName: String): RangeQueryDefinition = copy(queryName = queryName.some)
}

case class RawQueryDefinition(json: String) extends QueryDefinition
