package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class HasChildQueryDefinition(`type`: String,
                                   query: QueryDefinition,
                                   scoreMode: String,
                                   boost: Option[Double] = None,
                                   ignoreUnmapped: Option[Boolean] = None,
                                   innerHit: Option[InnerHitDefinition] = None,
                                   minMaxChildren: Option[(Int, Int)] = None,
                                   queryName: Option[String] = None)
  extends QueryDefinition {

  def boost(boost: Double): HasChildQueryDefinition = copy(boost = Some(boost))
  def ignoreUnmapped(ignoreUnmapped: Boolean): HasChildQueryDefinition = copy(ignoreUnmapped = Some(ignoreUnmapped))
  def minMaxChildren(min: Int, max: Int): HasChildQueryDefinition = copy(minMaxChildren = (min, max).some)
  def innerHit(innerHit: InnerHitDefinition): HasChildQueryDefinition = copy(innerHit = Some(innerHit))
  def queryName(queryName: String): HasChildQueryDefinition = copy(queryName = Some(queryName))
}

object ScoreMode {
  val First = "First"
  val Avg = "Avg"
  val Max = "Max"
  val Sum = "Sum"
  val Min = "Min"
  val Multiply = "Multiply"
}
