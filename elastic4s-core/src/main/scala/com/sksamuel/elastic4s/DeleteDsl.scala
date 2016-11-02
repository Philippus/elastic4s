package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.search.QueryDsl
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.{Client, Requests}
import org.elasticsearch.index.VersionType

import scala.concurrent.Future
import scala.language.implicitConversions

trait DeleteDsl extends QueryDsl {

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

  class DeleteByIdExpectsFrom(id: Any) {
    def from(index: String): DeleteByIdDefinition = DeleteByIdDefinition(IndexAndTypes(index), id)
    def from(indexAndTypes: IndexAndTypes): DeleteByIdDefinition = DeleteByIdDefinition(indexAndTypes, id)
  }

  implicit object DeleteByIdDefinitionExecutable
    extends Executable[DeleteByIdDefinition, DeleteResponse, DeleteResponse] {
    override def apply(c: Client, t: DeleteByIdDefinition): Future[DeleteResponse] = {
      injectFuture(c.delete(t.build, _))
    }
  }
}

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
