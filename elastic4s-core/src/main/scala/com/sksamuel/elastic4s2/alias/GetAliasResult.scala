package com.sksamuel.elastic4s2.alias

import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.cluster.metadata.AliasMetaData
import scala.collection.JavaConverters._

case class GetAliasResult(response: GetAliasesResponse) {

  def aliases: Map[String, Seq[AliasMetaData]] = {
    response.getAliases.keysIt().asScala.map(key => key -> response.getAliases.get(key).asScala).toMap
  }
}
