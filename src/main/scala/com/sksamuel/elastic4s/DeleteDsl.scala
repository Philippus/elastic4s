package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import org.elasticsearch.action.deletebyquery.DeleteByQueryAction
import org.elasticsearch.action.delete.DeleteAction

/** @author Stephen Samuel */
trait DeleteDsl extends QueryDsl {

  def delete: DeleteExpectsIdOrFrom = new DeleteExpectsIdOrFrom
  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

  class DeleteExpectsIdOrFrom {
    def id(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
    def from(kv: (String, String)): DeleteByQueryExpectsWhere = from(kv._1, kv._2)
    def from(index: String): DeleteByQueryExpectsWhere = index.split("/").toList match {
      case i :: Nil => from(i, null)
      case i :: t :: Nil => from(i, t)
      case _ => throw new IllegalArgumentException("from arg must be in the form index/type")
    }
    def from(indexes: Seq[String]) = new DeleteByQueryExpectsType(indexes)
    def from(index: String, `type`: String): DeleteByQueryExpectsWhere =
      new DeleteByQueryExpectsWhere(Seq(index), Seq(`type`))
  }

  class DeleteByQueryExpectsType(indexes: Seq[String]) {
    def types(types: Seq[String]): DeleteByQueryExpectsWhere = new DeleteByQueryExpectsWhere(indexes, types)
  }

  class DeleteByIdExpectsFrom(id: Any) {
    def from(index: String): DeleteByIdDefinition = index.split("/").toList match {
      case i :: Nil => from(i, null)
      case i :: t :: Nil => from(i, t)
      case _ => throw new IllegalArgumentException("from arg must be in the form index/type")
    }
    def from(kv: (String, String)): DeleteByIdDefinition = from(kv._1, kv._2)
    def from(index: String, `type`: String): DeleteByIdDefinition = new DeleteByIdDefinition(index, `type`, id)
  }

  @deprecated("use delete id <id> from <index/type>", "1.0")
  implicit def string2where(from: String): DeleteByQueryExpectsWhere = {
    from.split("/").toList match {
      case index :: Nil => new DeleteByQueryExpectsWhere(Seq(index), null)
      case index :: t :: Nil => new DeleteByQueryExpectsWhere(Seq(index), Seq(t))
      case _ => throw new IllegalArgumentException("from arg must be in the form index/type")
    }
  }
  implicit def string2indextype(index: String): IndexType = new IndexType(index)
  implicit def string2indextype(indexes: String*): IndexType = new IndexType(indexes: _*)
  class IndexType(indexes: String*) {
    def types(t: String): DeleteByQueryExpectsWhere = new DeleteByQueryExpectsWhere(indexes, Seq(t))
    def types(types: String*): DeleteByQueryExpectsWhere = new DeleteByQueryExpectsWhere(indexes, types)
    def types(types: Iterable[String]): DeleteByQueryExpectsWhere = new DeleteByQueryExpectsWhere(indexes, types.toSeq)
  }
  implicit def tuple2delete(tuple: (String, Any)) = {
    tuple._1.split("/").toList match {
      case index :: Nil => new DeleteByIdDefinition(index, null, tuple._2.toString)
      case index :: t :: Nil => new DeleteByIdDefinition(index, t, tuple._2.toString)
      case _ => throw new IllegalArgumentException("from must be in the form index/type")
    }
  }

  class DeleteByIdDefinition(index: String, `type`: String, id: Any)
    extends RequestDefinition(DeleteAction.INSTANCE) with BulkCompatibleDefinition {
    private val builder = Requests.deleteRequest(index).`type`(`type`).id(id.toString)
    def build = builder
  }

  class DeleteByQueryExpectsWhere(indexes: Seq[String], types: Seq[String]) {
    def where(query: String): DeleteByQueryDefinition = where(new StringQueryDefinition(query))
    def where(query: QueryDefinition): DeleteByQueryDefinition = new DeleteByQueryDefinition(indexes, types, query)
  }

  class DeleteByQueryDefinition(indexes: Seq[String], types: Seq[String], q: QueryDefinition)
    extends RequestDefinition(DeleteByQueryAction.INSTANCE) {
    private val builder = Requests.deleteByQueryRequest(indexes: _*).types(types: _*).query(q.builder)
    def build = builder
  }
}
