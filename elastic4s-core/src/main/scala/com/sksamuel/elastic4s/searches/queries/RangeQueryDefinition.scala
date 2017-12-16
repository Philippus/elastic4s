package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.ElasticDate
import com.sksamuel.exts.OptionImplicits._

case class RangeQueryDefinition(field: String,
                                boost: Option[Double] = None,
                                timeZone: Option[String] = None,
                                lte: Option[Any] = None,
                                gte: Option[Any] = None,
                                gt: Option[Any] = None,
                                lt: Option[Any] = None,
                                format: Option[String] = None,
                                queryName: Option[String] = None)
  extends MultiTermQueryDefinition {

  def boost(boost: Double): RangeQueryDefinition = copy(boost = boost.some)

  def lt(to: Long): RangeQueryDefinition = copy(lt = to.some)
  def lt(to: Double): RangeQueryDefinition = copy(lt = to.some)
  def lt(to: String): RangeQueryDefinition = copy(lt = to.some)
  def lt(date: ElasticDate): RangeQueryDefinition = copy(lt = date.some)

  def gt(f: Long): RangeQueryDefinition = copy(gt = f.some)
  def gt(f: Double): RangeQueryDefinition = copy(gt = f.some)
  def gt(f: String): RangeQueryDefinition = copy(gt = f.some)
  def gt(date: ElasticDate): RangeQueryDefinition = copy(gt = date.some)

  def lte(lte: Long): RangeQueryDefinition = copy(lte = lte.some)
  def lte(lte: Double): RangeQueryDefinition = copy(lte = lte.some)
  def lte(lte: String): RangeQueryDefinition = copy(lte = lte.some)
  def lte(date: ElasticDate): RangeQueryDefinition = copy(lte = date.some)

  def gte(gte: Long): RangeQueryDefinition = copy(gte = gte.some)
  def gte(gte: Double): RangeQueryDefinition = copy(gte = gte.some)
  def gte(gte: String): RangeQueryDefinition = copy(gte = gte.some)
  def gte(date: ElasticDate): RangeQueryDefinition = copy(gte = date.some)

  def format(fmt: String): RangeQueryDefinition = copy(format = fmt.some)

  def timeZone(timeZone: String): RangeQueryDefinition = copy(timeZone = timeZone.some)
  def queryName(queryName: String): RangeQueryDefinition = copy(queryName = queryName.some)
}

case class RawQueryDefinition(json: String) extends QueryDefinition
