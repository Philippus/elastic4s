package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ElasticDate
import com.sksamuel.exts.OptionImplicits._

sealed trait RangeRelation
object RangeRelation {
  case object Within     extends RangeRelation
  case object Intersects extends RangeRelation
  case object Contains   extends RangeRelation
}

case class RangeQuery(field: String,
                      boost: Option[Double] = None,
                      timeZone: Option[String] = None,
                      lte: Option[Any] = None,
                      gte: Option[Any] = None,
                      gt: Option[Any] = None,
                      lt: Option[Any] = None,
                      format: Option[String] = None,
                      queryName: Option[String] = None,
                      relation: Option[RangeRelation] = None // used by range fields
) extends MultiTermQuery {

  def boost(boost: Double): RangeQuery              = copy(boost = boost.some)
  def relation(relation: RangeRelation): RangeQuery = copy(relation = relation.some)

  def lt(to: Long): RangeQuery          = copy(lt = to.some)
  def lt(to: Double): RangeQuery        = copy(lt = to.some)
  def lt(to: String): RangeQuery        = copy(lt = to.some)
  def lt(date: ElasticDate): RangeQuery = copy(lt = date.some)

  def gt(f: Long): RangeQuery           = copy(gt = f.some)
  def gt(f: Double): RangeQuery         = copy(gt = f.some)
  def gt(f: String): RangeQuery         = copy(gt = f.some)
  def gt(date: ElasticDate): RangeQuery = copy(gt = date.some)

  def lte(lte: Long): RangeQuery         = copy(lte = lte.some)
  def lte(lte: Double): RangeQuery       = copy(lte = lte.some)
  def lte(lte: String): RangeQuery       = copy(lte = lte.some)
  def lte(date: ElasticDate): RangeQuery = copy(lte = date.some)

  def gte(gte: Long): RangeQuery         = copy(gte = gte.some)
  def gte(gte: Double): RangeQuery       = copy(gte = gte.some)
  def gte(gte: String): RangeQuery       = copy(gte = gte.some)
  def gte(date: ElasticDate): RangeQuery = copy(gte = date.some)

  def format(fmt: String): RangeQuery = copy(format = fmt.some)

  def timeZone(timeZone: String): RangeQuery   = copy(timeZone = timeZone.some)
  def queryName(queryName: String): RangeQuery = copy(queryName = queryName.some)
}

case class RawQuery(json: String) extends Query
