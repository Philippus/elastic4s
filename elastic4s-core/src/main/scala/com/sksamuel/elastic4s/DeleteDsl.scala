package com.sksamuel.elastic4s

import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.client.{Client, Requests}
import org.elasticsearch.index.VersionType

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait DeleteDsl extends QueryDsl with IndexesTypesDsl {

  class DeleteByIdExpectsFrom(id: Any) {
    def from(_index: String): DeleteByIdDefinition = new DeleteByIdDefinition(IndexesTypes(_index), id)
    def from(_indexes: String*): DeleteByIdExpectsTypes = from(_indexes)
    def from(_indexes: Iterable[String]): DeleteByIdExpectsTypes = new DeleteByIdExpectsTypes(_indexes, id)
    def from(indexType: IndexType): DeleteByIdDefinition = new DeleteByIdDefinition(IndexesTypes(indexType), id)
    def from(indexesTypes: IndexesTypes): DeleteByIdDefinition = new DeleteByIdDefinition(indexesTypes, id)
  }

  class DeleteByIdExpectsTypes(indexes: Iterable[String], id: Any) {
    def types(`type`: String): DeleteByIdDefinition = types(List(`type`))
    def types(_types: String*): DeleteByIdDefinition = types(_types)
    def types(_types: Iterable[String]): DeleteByIdDefinition =
      new DeleteByIdDefinition(IndexesTypes(indexes.toSeq, _types.toSeq), id)
  }

  implicit def string2indextype(index: String): IndexesType = new IndexesType(index)
  implicit def string2indextype(indexes: String*): IndexesType = new IndexesType(indexes: _*)

  class IndexesType(indexes: String*)

  implicit object DeleteByIdDefinitionExecutable
    extends Executable[DeleteByIdDefinition, DeleteResponse, DeleteResponse] {
    override def apply(c: Client, t: DeleteByIdDefinition): Future[DeleteResponse] = {
      injectFuture(c.delete(t.build, _))
    }
  }
}

class DeleteByIdDefinition(indexType: IndexesTypes, id: Any) extends BulkCompatibleDefinition {

  private[elastic4s] val builder = Requests.deleteRequest(indexType.index).`type`(indexType.typ.orNull).id(id.toString)

  def types(_type: String): DeleteByIdDefinition = {
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
  def refresh(refresh: Boolean): DeleteByIdDefinition = {
    builder.refresh(refresh)
    this
  }
  def version(version: Int): DeleteByIdDefinition = {
    builder.version(version)
    this
  }
  def versionType(versionType: VersionType): DeleteByIdDefinition = {
    builder.versionType(versionType)
    this
  }
  def build = builder
}