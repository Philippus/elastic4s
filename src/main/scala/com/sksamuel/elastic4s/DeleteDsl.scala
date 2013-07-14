package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
trait DeleteDsl extends QueryDsl {

  implicit def string2where(from: String): DeleteByQueryExpectsWhere = {
    from.split("/").toList match {
      case index :: Nil => new DeleteByQueryExpectsWhere(Seq(index), null)
      case index :: t :: Nil => new DeleteByQueryExpectsWhere(Seq(index), Seq(t))
      case _ => throw new IllegalArgumentException("from must be in the form index/type")
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

  class DeleteByIdDefinition(index: String, `type`: String, id: String) extends BulkCompatibleRequest {
    val builder = Requests.deleteRequest(index).`type`(`type`).id(id)
  }

  class DeleteByQueryExpectsWhere(indexes: Seq[String], types: Seq[String]) {
    def where(query: String): DeleteByQueryDefinition = where(new StringQueryDefinition(query))
    def where(query: QueryDefinition): DeleteByQueryDefinition = new DeleteByQueryDefinition(indexes, types, query)
  }

  class DeleteByQueryDefinition(indexes: Seq[String], types: Seq[String], q: QueryDefinition) {
    val builder = Requests.deleteByQueryRequest(indexes: _*).types(types: _*).query(q.builder)
  }
}
