package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.query.QueryStringQueryDefinition
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.index.{IndexAction, IndexRequestBuilder, IndexResponse}
import org.elasticsearch.action.percolate.PercolateAction
import org.elasticsearch.client.Client
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentHelper}
import org.elasticsearch.percolator.{PercolateResponse, PercolatorService}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait PercolateDsl extends QueryDsl {

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
  require(index.nonEmpty, "index must not be null or empty")
  require(id.toString.nonEmpty, "id must not be null or empty")

  private[this] var _query: QueryDefinition = _
  private val _fields = new ListBuffer[(String, Any)]

  def build = {
    val source = XContentFactory.jsonBuilder().startObject().field("query", _query.builder)
    for ( tuple <- _fields ) {
      source.field(tuple._1, tuple._2)
    }
    source.endObject()
    new IndexRequestBuilder(ProxyClients.client, IndexAction.INSTANCE).setIndex(index)
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

case class PercolateDefinition(indexesAndTypes: IndexesAndTypes) {
  require(indexesAndTypes != null, "index must not be null or empty")

  private val _fields = new ListBuffer[(String, Any)]
  private var _rawDoc: Option[String] = None
  private var _id: Option[String] = None
  private[this] var _query: QueryDefinition = _

  def build = {
    val _type = indexesAndTypes.types.head
    //What happens if user provides many indicies? Is it supported?
    val index = indexesAndTypes.indexes.head

    def pb = new PercolateRequestBuilder(ProxyClients.client, PercolateAction.INSTANCE)
      .setIndices(indexesAndTypes.indexes: _*)
      .setDocumentType(_type)

    _id.map(id => pb.setGetRequest(new GetRequest(index, _type, id)))
    .getOrElse(pb.setSource(_doc))
    .request()
  }

  private[elastic4s] def _doc: XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()

    if (_query != null)
      source.field("query", _query.builder)

    _rawDoc match {
      case Some(doc) =>
        source.rawField("doc", new BytesArray(doc.getBytes("UTF-8")))
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

  def id(id: String):PercolateDefinition = {
    this._id = Some(id)
    this
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
