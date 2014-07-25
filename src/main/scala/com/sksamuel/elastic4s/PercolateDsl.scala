package com.sksamuel.elastic4s

import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.action.percolate.PercolateRequestBuilder
import org.elasticsearch.common.xcontent.{ XContentBuilder, XContentFactory }
import org.elasticsearch.percolator.PercolatorService

import scala.collection.mutable.ListBuffer

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
    private var _rawDoc: Option[String] = None
    private[this] var _query: QueryDefinition = _

    def build = new PercolateRequestBuilder(ProxyClients.client).setSource(_doc).setIndices(index).setDocumentType("doc").request()

    private[elastic4s] def _doc: XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()

      if (_query != null)
        source.field("query", _query.builder)

      _rawDoc match {
        case Some(doc) =>
          source.rawField("doc", doc.getBytes("UTF-8"))
        case None =>
          source.startObject("doc")
          for (tuple <- _fields) {
            source.field(tuple._1, tuple._2)
          }
          source.endObject()
      }
      source.endObject()
      source
    }

    def rawDoc(json: String): PercolateDefinition = {
      this._rawDoc = Some(json)
      this
    }

    def doc(fields: (String, Any)*): PercolateDefinition = {
      this._fields ++= fields
      this
    }

    def doc(fields: Map[String, Any]): PercolateDefinition = {
      this._fields ++= fields
      this
    }

    def query(string: String): PercolateDefinition = query(new StringQueryDefinition(string))
    def query(block: => QueryDefinition) = {
      _query = block
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
    private[this] var _query: QueryDefinition = _
    private val _fields = new ListBuffer[(String, Any)]
    def build = {
      val source = XContentFactory.jsonBuilder()
        .startObject().field("query", _query.builder)
      for (tuple <- _fields) {
        source.field(tuple._1, tuple._2)
      }
      source.endObject()
      new IndexRequestBuilder(ProxyClients.client).setIndex(index)
        .setType(PercolatorService.TYPE_NAME).setId(id).setRefresh(true)
        .setSource(source).request
    }

    def query(block: => QueryDefinition): RegisterDefinition = {
      _query = block
      this
    }
    def query(string: String) = {
      _query = new StringQueryDefinition(string)
      this
    }
    def fields(map: Map[String, Any]): RegisterDefinition = fields(map.toList)
    def fields(_fields: (String, Any)*): RegisterDefinition = fields(_fields.toIterable)
    def fields(iterable: Iterable[(String, Any)]) = {
      this._fields ++= iterable
      this
    }
  }
}
