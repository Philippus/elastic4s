package com.sksamuel.elastic4s.searches.queries

case class HasParentQueryDefinition(`type`: String,
                                    query: QueryDefinition,
                                    score: Boolean,
                                    boost: Option[Double] = None,
                                    ignoreUnmapped: Option[Boolean] = None,
                                    innerHit: Option[InnerHitDefinition] = None,
                                    queryName: Option[String] = None)
    extends QueryDefinition {

  def boost(boost: Double): HasParentQueryDefinition                    = copy(boost = Some(boost))
  def ignoreUnmapped(ignoreUnmapped: Boolean): HasParentQueryDefinition = copy(ignoreUnmapped = Some(ignoreUnmapped))
  def innerHit(innerHit: InnerHitDefinition): HasParentQueryDefinition  = copy(innerHit = Some(innerHit))
  def queryName(queryName: String): HasParentQueryDefinition            = copy(queryName = Some(queryName))
}
