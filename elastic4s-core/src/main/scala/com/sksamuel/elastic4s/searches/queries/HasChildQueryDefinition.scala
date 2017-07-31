package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._
import org.apache.lucene.search.join.ScoreMode

case class HasChildQueryDefinition(`type`: String,
                                   query: QueryDefinition,
                                   scoreMode: ScoreMode,
                                   boost: Option[Double] = None,
                                   ignoreUnmapped: Option[Boolean] = None,
                                   innerHit: Option[InnerHitDefinition] = None,
                                   minChildren: Option[Int] = None,
                                   maxChildren: Option[Int] = None,
                                   queryName: Option[String] = None)
  extends QueryDefinition {

  def boost(boost: Double): HasChildQueryDefinition = copy(boost = Some(boost))
  def ignoreUnmapped(ignoreUnmapped: Boolean): HasChildQueryDefinition = copy(ignoreUnmapped = Some(ignoreUnmapped))
  def minMaxChildren(min: Int, max: Int): HasChildQueryDefinition = minChildren(min).maxChildren(max)
  def minChildren(min: Int): HasChildQueryDefinition = copy(minChildren = min.some)
  def maxChildren(max: Int): HasChildQueryDefinition = copy(maxChildren = max.some)
  def innerHit(innerHit: InnerHitDefinition): HasChildQueryDefinition = copy(innerHit = Some(innerHit))
  def queryName(queryName: String): HasChildQueryDefinition = copy(queryName = Some(queryName))
}
