package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s._
import org.elasticsearch.action.support.IndicesOptions
import com.sksamuel.exts.OptionImplicits._

case class OpenIndexDefinition(indexes: Indexes)
case class CloseIndexDefinition(indexes: Indexes)
case class GetSegmentsDefinition(indexes: Indexes)
case class IndexExistsDefinition(index: String)
case class TypesExistsDefinition(indexes: Seq[String], types: Seq[String])
case class AliasExistsDefinition(alias: String)
case class IndicesStatsDefinition(indexes: Indexes)

case class ClearCacheDefinition(indexes: Seq[String],
                                fieldDataCache: Option[Boolean] = None,
                                requestCache: Option[Boolean] = None,
                                indicesOptions: Option[IndicesOptions] = None,
                                queryCache: Option[Boolean] = None,
                                fields: Seq[String] = Nil)

case class FlushIndexDefinition(indexes: Seq[String],
                                waitIfOngoing: Option[Boolean] = None,
                                force: Option[Boolean] = None) {
  def force(force: Boolean): FlushIndexDefinition = copy(force = force.some)
  def waitIfOngoing(waitIfOngoing: Boolean): FlushIndexDefinition = copy(waitIfOngoing = waitIfOngoing.some)
}

case class RefreshIndexDefinition(indexes: Seq[String])
