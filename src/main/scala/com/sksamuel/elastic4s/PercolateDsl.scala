package com.sksamuel.elastic4s

import scala.collection.mutable.ListBuffer
import org.elasticsearch.action.percolate.PercolateRequestBuilder
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}
import org.elasticsearch.action.index.IndexRequestBuilder

/** @author Stephen Samuel */
trait PercolateDsl extends QueryDsl {

  implicit def any2register(id: AnyVal) = new RegisterExpectsIndex(id.toString)
  implicit def string2register(id: String) = new RegisterExpectsIndex(id)
  implicit def string2percolate(index: String) = new PercolateDefinition(index)

  def percolate = new PercolateExpectsIndex
  class PercolateExpectsIndex {
    def in(index: String) = new PercolateDefinition(index)
  }

  class PercolateDefinition(index: String) {

    private val _fields = new ListBuffer[(String, Any)]

    def build = new PercolateRequestBuilder(null, index, "type1").setSource(_doc).request()

    private[elastic4s] def _doc: XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject().startObject("doc")
      for ( tuple <- _fields ) {
        source.field(tuple._1, tuple._2)
      }
      source.endObject().endObject()
      source
    }

    def doc(fields: (String, Any)*): PercolateDefinition = {
      this._fields ++= fields
      this
    }

    def doc(fields: Map[String, Any]): PercolateDefinition = {
      this._fields ++= fields
      this
    }
  }

  def register = new RegisterExpectsId
  class RegisterExpectsId {
    def id(id: Any) = new RegisterExpectsIndex(id.toString)
  }

  class RegisterExpectsIndex(id: String) {
    def into(index: String) = new RegisterDefinition(index, id)
  }

  class RegisterDefinition(index: String, id: String) {
    var _query: QueryDefinition = _
    def build = {
      val req = new IndexRequestBuilder(null).setIndex("_percolator").setType(index).setId(id).setRefresh(true)
      val source = XContentFactory.jsonBuilder().startObject()
      source.field("query", _query.builder)
      source.endObject()
      req.setSource(source)
      req
    }
    def query(block: => QueryDefinition): RegisterDefinition = {
      _query = block
      this
    }
    def query(string: String) = {
      _query = new StringQueryDefinition(string)
      this
    }
  }
}
