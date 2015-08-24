package com.sksamuel.elastic4s

import org.elasticsearch.action.index.{IndexRequestBuilder, IndexResponse}
import org.elasticsearch.action.percolate.{PercolateRequestBuilder, PercolateResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.{XContentHelper, XContentBuilder, XContentFactory}
import org.elasticsearch.percolator.PercolatorService

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait PercolateDsl extends QueryDsl {

  class RegisterExpectsIndex(id: String) {
    def into(index: String) = new RegisterDefinition(index, id)
  }

  implicit object RegisterDefinitionExecutable extends Executable[RegisterDefinition, IndexResponse, IndexResponse] {
    override def apply(c: Client, t: RegisterDefinition): Future[IndexResponse] = {
      injectFuture(c.index(t.build, _))
    }
  }

  implicit object PercolateDefinitionExecutable
    extends Executable[PercolateDefinition, PercolateResponse, PercolateResponse] {
    override def apply(c: Client, t: PercolateDefinition): Future[PercolateResponse] = {
      injectFuture(c.percolate(t.build, _))
    }
  }

  implicit object RegisterDefinitionShow extends Show[RegisterDefinition] {
    override def show(f: RegisterDefinition): String = XContentHelper.convertToJson(f.build.source, true, true)
  }

  implicit class RegisterDefinitionShowOps(f: RegisterDefinition) {
    def show: String = RegisterDefinitionShow.show(f)
  }

  implicit object PercolateDefinitionShow extends Show[PercolateDefinition] {
    override def show(f: PercolateDefinition): String = XContentHelper.convertToJson(f.build.source, true, true)
  }

  implicit class PercolateDefinitionShowOps(f: PercolateDefinition) {
    def show: String = PercolateDefinitionShow.show(f)
  }
}

class RegisterDefinition(index: String, id: String) extends BulkCompatibleDefinition {

  private[this] var _query: QueryDefinition = _
  private val _fields = new ListBuffer[(String, Any)]

  def build = {
    val source = XContentFactory.jsonBuilder().startObject().field("query", _query.builder)
    for ( tuple <- _fields ) {
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
    _query = new QueryStringQueryDefinition(string)
    this
  }
  def fields(map: Map[String, Any]): RegisterDefinition = fields(map.toList)
  def fields(_fields: (String, Any)*): RegisterDefinition = fields(_fields.toIterable)
  def fields(iterable: Iterable[(String, Any)]) = {
    this._fields ++= iterable
    this
  }
}

class PercolateDefinition(indexType: IndexesTypes) {

  private val _fields = new ListBuffer[(String, Any)]
  private var _rawDoc: Option[String] = None
  private[this] var _query: QueryDefinition = _

  def build = new PercolateRequestBuilder(ProxyClients.client)
    .setSource(_doc)
    .setIndices(indexType.index)
    .setDocumentType(indexType.types.head)
    .request()

  private[elastic4s] def _doc: XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()

    if (_query != null)
      source.field("query", _query.builder)

    _rawDoc match {
      case Some(doc) =>
        source.rawField("doc", doc.getBytes("UTF-8"))
      case None =>
        source.startObject("doc")
        for ( tuple <- _fields ) {
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

  def query(string: String): PercolateDefinition = query(new QueryStringQueryDefinition(string))
  def query(block: => QueryDefinition) = {
    _query = block
    this
  }
}
