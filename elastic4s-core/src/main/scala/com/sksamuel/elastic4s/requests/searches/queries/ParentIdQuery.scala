package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class ParentIdQuery(`type`: String,
                         id: String,
                         ignoreUnmapped: Option[Boolean] = None,
                         boost: Option[Double] = None,
                         queryName: Option[String] = None)
    extends Query {

  def queryName(name: String): ParentIdQuery         = copy(queryName = name.some)
  def boost(boost: Double): ParentIdQuery            = copy(boost = boost.some)
  def ignoreUnmapped(ignore: Boolean): ParentIdQuery = copy(ignoreUnmapped = ignore.some)
}
