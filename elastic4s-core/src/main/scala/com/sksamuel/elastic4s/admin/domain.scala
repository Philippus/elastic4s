package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s._
import org.elasticsearch.action.support.IndicesOptions

case class OpenIndexDefinition(indexes: Indexes)
case class CloseIndexDefinition(indexes: Indexes)
case class GetSegmentsDefinition(indexes: Indexes)
case class IndexExistsDefinition(index: String)
case class TypesExistsDefinition(indexes: Seq[String], types: Seq[String])
case class IndicesStatsDefinition(indexes: Indexes)

case class ClearCacheDefinition(indexes: Seq[String],
                                fieldDataCache: Option[Boolean] = None,
                                requestCache: Option[Boolean] = None,
                                indicesOptions: Option[IndicesOptions] = None,
                                queryCache: Option[Boolean] = None,
                                fields: Seq[String] = Nil)

case class FlushIndexDefinition(indexes: Seq[String])
case class RefreshIndexDefinition(indexes: Seq[String])
