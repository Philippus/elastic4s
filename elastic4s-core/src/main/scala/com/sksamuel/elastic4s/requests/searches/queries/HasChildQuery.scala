package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.searches.ScoreMode
import com.sksamuel.exts.OptionImplicits._

case class HasChildQuery(`type`: String,
                         query: Query,
                         scoreMode: ScoreMode,
                         boost: Option[Double] = None,
                         ignoreUnmapped: Option[Boolean] = None,
                         innerHit: Option[InnerHit] = None,
                         minChildren: Option[Int] = None,
                         maxChildren: Option[Int] = None,
                         queryName: Option[String] = None)
    extends Query {

  def boost(boost: Double): HasChildQuery                    = copy(boost = Some(boost))
  def ignoreUnmapped(ignoreUnmapped: Boolean): HasChildQuery = copy(ignoreUnmapped = Some(ignoreUnmapped))
  def minMaxChildren(min: Int, max: Int): HasChildQuery      = minChildren(min).maxChildren(max)
  def minChildren(min: Int): HasChildQuery                   = copy(minChildren = min.some)
  def maxChildren(max: Int): HasChildQuery                   = copy(maxChildren = max.some)
  def innerHit(innerHit: InnerHit): HasChildQuery            = copy(innerHit = Some(innerHit))

  // create an inner hit with the default options
  def innerHit(name:String): HasChildQuery                   = copy(innerHit = Some(InnerHit(name)))
  def queryName(queryName: String): HasChildQuery            = copy(queryName = Some(queryName))
}
