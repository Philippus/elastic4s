package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.index.query.MultiTermQueryBuilder

case class PrefixQueryDefinition(field: String,
                                 prefix: Any,
                                 boost: Option[Double] = None,
                                 queryName: Option[String] = None,
                                 rewrite: Option[String] = None)
  extends MultiTermQueryDefinition {

  def queryName(queryName: String): PrefixQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): PrefixQueryDefinition = copy(boost = boost.some)
  def rewrite(rewrite: String): PrefixQueryDefinition = copy(rewrite = rewrite.some)
  override def builder: MultiTermQueryBuilder = ???
}
