package com.sksamuel.elastic4s.requests.searches.queries

case class HasParentQuery(parentType: String,
                          query: Query,
                          score: Boolean,
                          boost: Option[Double] = None,
                          ignoreUnmapped: Option[Boolean] = None,
                          innerHit: Option[InnerHit] = None,
                          queryName: Option[String] = None)
    extends Query {

  def boost(boost: Double): HasParentQuery                    = copy(boost = Some(boost))
  def ignoreUnmapped(ignoreUnmapped: Boolean): HasParentQuery = copy(ignoreUnmapped = Some(ignoreUnmapped))
  def innerHit(innerHit: InnerHit): HasParentQuery            = copy(innerHit = Some(innerHit))
  def queryName(queryName: String): HasParentQuery            = copy(queryName = Some(queryName))
}
