package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.apache.lucene.search.join.ScoreMode
import org.elasticsearch.index.query.{HasChildQueryBuilder, QueryBuilders}
import com.sksamuel.exts.OptionImplicits._

case class HasChildQueryDefinition(`type`: String,
                                   query: QueryDefinition,
                                   scoreMode: ScoreMode,
                                   minMaxChildren: Option[(Int, Int)] = None,
                                   boost: Option[Double] = None,
                                   ignoreUnmapped: Option[Boolean] = None,
                                   innerHit: Option[InnerHitDefinition] = None,
                                   queryName: Option[String] = None)
  extends QueryDefinition {

  def builder: HasChildQueryBuilder = {
    val builder = QueryBuilders.hasChildQuery(`type`, QueryBuilderFn(query), scoreMode)
    minMaxChildren.foreach { case (min, max) => builder.minMaxChildren(min, max) }
    boost.map(_.toFloat).foreach(builder.boost)
    innerHit.map(_.builder).foreach(builder.innerHit)
    ignoreUnmapped.foreach(builder.ignoreUnmapped)
    queryName.foreach(builder.queryName)
    builder
  }

  def boost(boost: Double): HasChildQueryDefinition = copy(boost = Some(boost))
  def ignoreUnmapped(ignoreUnmapped: Boolean): HasChildQueryDefinition = copy(ignoreUnmapped = Some(ignoreUnmapped))
  def minMaxChildren(min: Int, max: Int): HasChildQueryDefinition = copy(minMaxChildren = (min, max).some)
  def innerHit(innerHit: InnerHitDefinition): HasChildQueryDefinition = copy(innerHit = Some(innerHit))
  def queryName(queryName: String): HasChildQueryDefinition = copy(queryName = Some(queryName))
}
