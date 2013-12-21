package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import org.elasticsearch.action.deletebyquery.DeleteByQueryAction
import org.elasticsearch.action.delete.DeleteAction

/** @author Stephen Samuel */
trait DeleteDsl extends QueryDsl with IndexesTypesDsl {

  def delete: DeleteExpectsIdOrFrom = new DeleteExpectsIdOrFrom
  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

  class DeleteExpectsIdOrFrom {
    def id(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
    def from(indexesTypes: IndexesTypes): DeleteByQueryExpectsWhere = new DeleteByQueryExpectsWhere(indexesTypes)
    def from(index: String): DeleteByQueryExpectsWhere = from(IndexesTypes(index))
    def from(indexes: String*): DeleteByQueryExpectsType = from(indexes)
    def from(indexes: Iterable[String]): DeleteByQueryExpectsType = new DeleteByQueryExpectsType(indexes.toSeq)
  }

  class DeleteByQueryExpectsType(indexes: Seq[String]) {
    def types(_types: String*): DeleteByQueryExpectsWhere = types(_types)
    def types(_types: Iterable[String]): DeleteByQueryExpectsWhere =
      new DeleteByQueryExpectsWhere(IndexesTypes(indexes, _types.toSeq))
  }

  class DeleteByIdExpectsFrom(id: Any) {
    def from(_index: String): DeleteByIdDefinition = new DeleteByIdDefinition(IndexesTypes(_index), id)
    def from(_indexes: String*): DeleteByIdExpectsTypes = from(_indexes)
    def from(_indexes: Iterable[String]): DeleteByIdExpectsTypes = new DeleteByIdExpectsTypes(_indexes, id)
    def from(indexesTypes: IndexesTypes): DeleteByIdDefinition = new DeleteByIdDefinition(indexesTypes, id)
  }

  class DeleteByIdExpectsTypes(indexes: Iterable[String], id: Any) {
    def types(`type`: String): DeleteByIdDefinition = types(List(`type`))
    def types(_types: String*): DeleteByIdDefinition = types(_types)
    def types(_types: Iterable[String]): DeleteByIdDefinition =
      new DeleteByIdDefinition(IndexesTypes(indexes.toSeq, _types.toSeq), id)
  }

  @deprecated("use delete from <from> from <index/type>", "0.90.8")
  implicit def string2where(from: String): DeleteByQueryExpectsWhere =
    new DeleteByQueryExpectsWhere(IndexesTypes(from))
  implicit def string2indextype(index: String): IndexType = new IndexType(index)
  implicit def string2indextype(indexes: String*): IndexType = new IndexType(indexes: _*)
  class IndexType(indexes: String*) {
    def types(types: String*): DeleteByQueryExpectsWhere = new DeleteByQueryExpectsWhere(IndexesTypes(indexes, types))
    def types(types: Iterable[String]): DeleteByQueryExpectsWhere =
      new DeleteByQueryExpectsWhere(IndexesTypes(indexes, types.toSeq))
  }

  class DeleteByIdDefinition(indexType: IndexesTypes, id: Any)
    extends RequestDefinition(DeleteAction.INSTANCE) with BulkCompatibleDefinition {
    private val builder = Requests.deleteRequest(indexType.index).`type`(indexType.typ.orNull).id(id.toString)
    def types(_type: String): DeleteByIdDefinition = {
      builder.`type`(_type)
      this
    }
    def build = builder
  }

  class DeleteByQueryExpectsWhere(indexesTypes: IndexesTypes) {
    def types(_types: String*): DeleteByQueryExpectsWhere = types(_types)
    def types(_types: Iterable[String]): DeleteByQueryExpectsWhere =
      new DeleteByQueryExpectsWhere(indexesTypes.copy(types = _types.toSeq))
    def where(query: String): DeleteByQueryDefinition = where(new StringQueryDefinition(query))
    def where(query: QueryDefinition): DeleteByQueryDefinition = new DeleteByQueryDefinition(indexesTypes, query)
  }

  class DeleteByQueryDefinition(indexesTypes: IndexesTypes, q: QueryDefinition)
    extends RequestDefinition(DeleteByQueryAction.INSTANCE) {

    private val builder = Requests.deleteByQueryRequest(indexesTypes.indexes: _*)
      .types(indexesTypes.types: _*)
      .query(q.builder)

    def types(types: String*): DeleteByQueryDefinition = {
      builder.types(types.toSeq: _*)
      this
    }
    def build = builder
  }
}
