package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.{BulkCompatibleDefinition, IndexAndTypes}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.Requests
import org.elasticsearch.index.VersionType

case class DeleteByIdDefinition(indexType: IndexAndTypes, id: Any) extends BulkCompatibleDefinition {

  private[elastic4s] val builder = {
    Requests.deleteRequest(indexType.index).`type`(indexType.types.headOption.orNull).id(id.toString)
  }

  def `type`(_type: String): DeleteByIdDefinition = {
    builder.`type`(_type)
    this
  }

  def parent(parent: String): DeleteByIdDefinition = {
    builder.parent(parent)
    this
  }

  def routing(routing: String): DeleteByIdDefinition = {
    builder.routing(routing)
    this
  }

  def refresh(refresh: RefreshPolicy): this.type = {
    builder.setRefreshPolicy(refresh)
    this
  }

  def version(version: Long): DeleteByIdDefinition = {
    builder.version(version)
    this
  }

  def versionType(versionType: VersionType): DeleteByIdDefinition = {
    builder.versionType(versionType)
    this
  }

  def build = builder
}
