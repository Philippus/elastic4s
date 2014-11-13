package com.sksamuel.elastic4s

import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.action.percolate.PercolateRequestBuilder
import org.elasticsearch.common.xcontent.{ XContentBuilder, XContentFactory }
import org.elasticsearch.percolator.PercolatorService

import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
trait PercolateDsl extends QueryDsl {

  @deprecated("Use the register id X into Y syntax", "1.4.1")
  implicit def any2register(id: AnyVal): RegisterExpectsIndexImplicit = new RegisterExpectsIndexImplicit(id.toString)

  @deprecated("Use the register id X into Y syntax", "1.4.1")
  implicit def string2register(id: String): RegisterExpectsIndexImplicit = new RegisterExpectsIndexImplicit(id)

  @deprecated("Use the percolate in X", "1.4.1")
  implicit def string2percolate(index: String): PercolateDefinitionImplicit = new PercolateDefinitionImplicit(IndexesTypes(index))

  class PercolateDefinitionImplicit(indexType: IndexesTypes) extends PercolateDefinition(indexType) {
    @deprecated("Use the percolate in X", "1.4.1")
    override def doc(fields: (String, Any)*): PercolateDefinition = super.doc(fields: _*)
    @deprecated("Use the percolate in X", "1.4.1")
    override def doc(fields: Map[String, Any]): PercolateDefinition = super.doc(fields)
    @deprecated("Use the percolate in X", "1.4.1")
    override def query(string: String): PercolateDefinition = super.query(string)
    @deprecated("Use the percolate in X", "1.4.1")
    override def query(block: => QueryDefinition): PercolateDefinition = super.query(block)
  }

  class PercolateDefinition(indexType: IndexesTypes) {

    private val _fields = new ListBuffer[(String, Any)]
    private var _rawDoc: Option[String] = None
    private[this] var _query: QueryDefinition = _

    def build = new PercolateRequestBuilder(ProxyClients.client).setSource(_doc).setIndices(indexType.index).setDocumentType(indexType.types.head).request()

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

  class RegisterExpectsIndex(id: String) {
    def into(index: String) = new RegisterDefinition(index, id)
  }

  class RegisterExpectsIndexImplicit(id: String) {
    @deprecated("Use the register id X into Y syntax", "1.4.0")
    def into(index: String) = new RegisterDefinition(index, id)
  }

  class RegisterDefinition(index: String, id: String) extends BulkCompatibleDefinition {
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
