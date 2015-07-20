package com.sksamuel.elastic4s

import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.deletebyquery.{DeleteByQueryRequestBuilder, DeleteByQueryResponse}
import org.elasticsearch.action.support.QuerySourceBuilder
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.client.{Client, Requests}
import org.elasticsearch.index.VersionType

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait DeleteDsl extends QueryDsl with IndexesTypesDsl {

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

  class DeleteByQueryExpectsType(indexes: Seq[String]) {
    def types(_types: String*): DeleteByQueryExpectsClause = types(_types)
    def types(_types: Iterable[String]): DeleteByQueryExpectsClause =
      new DeleteByQueryExpectsClause(IndexesTypes(indexes, _types.toSeq))
  }

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

  class IndexesType(indexes: String*) {
    def types(types: String*): DeleteByQueryExpectsClause = new DeleteByQueryExpectsClause(IndexesTypes(indexes, types))
    def types(types: Iterable[String]): DeleteByQueryExpectsClause =
      new DeleteByQueryExpectsClause(IndexesTypes(indexes, types.toSeq))
  }

  class DeleteByQueryExpectsClause(indexesTypes: IndexesTypes) {
    def types(_types: String*): DeleteByQueryExpectsClause = types(_types)
    def types(_types: Iterable[String]): DeleteByQueryExpectsClause = new
        DeleteByQueryExpectsClause(indexesTypes.copy(types = _types.toSeq))
    def id(id: Any): DeleteByIdDefinition = new DeleteByIdDefinition(indexesTypes, id)
    def where(query: String): DeleteByQueryDefinition = where(new SimpleStringQueryDefinition(query))
    def where(query: QueryDefinition): DeleteByQueryDefinition = new DeleteByQueryDefinition(indexesTypes, query)
  }

  implicit object DeleteByIdDefinitionExecutable extends Executable[DeleteByIdDefinition, DeleteResponse] {
    override def apply(c: Client, t: DeleteByIdDefinition): Future[DeleteResponse] = {
      injectFuture(c.delete(t.build, _))
    }
  }

  implicit object DeleteByQueryDefinitionExecutable extends Executable[DeleteByQueryDefinition, DeleteByQueryResponse] {
    override def apply(c: Client, t: DeleteByQueryDefinition): Future[DeleteByQueryResponse] = {
      injectFuture(c.deleteByQuery(t.build, _))
    }
  }
}

class DeleteByIdDefinition(indexType: IndexesTypes, id: Any) extends BulkCompatibleDefinition {
  private val builder = Requests.deleteRequest(indexType.index).`type`(indexType.typ.orNull).id(id.toString)
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

class DeleteByQueryDefinition(indexesTypes: IndexesTypes, q: QueryDefinition) {

  private val builder: DeleteByQueryRequestBuilder =
    new DeleteByQueryRequestBuilder(ProxyClients.client)
      .setIndices(indexesTypes.indexes: _*)
      .setTypes(indexesTypes.types: _*)

  def types(types: String*): DeleteByQueryDefinition = {
    builder.setTypes(types.toSeq: _*)
    this
  }
  def routing(routing: String): DeleteByQueryDefinition = {
    builder.setRouting(routing)
    this
  }
  def replicationType(repType: String): DeleteByQueryDefinition = {
    builder.setReplicationType(repType)
    this
  }
  def replicationType(repType: ReplicationType): DeleteByQueryDefinition = {
    builder.setReplicationType(repType)
    this
  }
  def consistencyLevel(consistencyLevel: WriteConsistencyLevel): DeleteByQueryDefinition = {
    builder.setConsistencyLevel(consistencyLevel)
    this
  }

  def build = {
    val req = builder.request()

    // need to set the query on the request - workaround for ES internals
    val qsb = new QuerySourceBuilder().setQuery(q.builder)
    req.source(qsb)
  }
}
