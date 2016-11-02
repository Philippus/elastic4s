package com.sksamuel.elastic4s2

import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.index.IndexRequest.OpType
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentHelper}
import org.elasticsearch.index.VersionType

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait IndexDsl {

  def indexInto(index: String, `type`: String): IndexDefinition = new IndexDefinition(index, `type`)
  def indexInto(indexType: IndexAndType): IndexDefinition = new IndexDefinition(indexType.index, indexType.`type`)

  @deprecated("use indexInto(indexType)", "3.0.0")
  def index(kv: (String, String)): IndexDefinition = new IndexDefinition(kv._1, kv._2)

  implicit object IndexDefinitionExecutable
    extends Executable[IndexDefinition, IndexResponse, IndexResult] {
    override def apply(c: Client, t: IndexDefinition): Future[IndexResult] = {
      injectFutureAndMap(c.index(t.build, _))(IndexResult.apply)
    }
  }

  implicit object IndexDefinitionShow extends Show[IndexDefinition] {
    override def show(f: IndexDefinition): String = XContentHelper.convertToJson(f.build.source, true, true)
  }

  implicit class IndexDefinitionShowOps(f: IndexDefinition) {
    def show: String = IndexDefinitionShow.show(f)
  }
}

case class IndexResult(original: IndexResponse) {

  @deprecated("use id", "3.0.0")
  def getId = id

  @deprecated("use `type`", "3.0.0")
  def getType = `type`

  @deprecated("use index", "3.0.0")
  def getIndex = index

  @deprecated("use version", "3.0.0")
  def getVersion = original.getVersion

  @deprecated("use created", "3.0.0")
  def isCreated: Boolean = created

  def id = original.getId
  def index = original.getIndex
  def `type` = original.getType
  def version: Long = original.getVersion
  def documentRef = DocumentRef(index, `type`, id)

  def created: Boolean = original.getResult == Result.CREATED
}

class IndexDefinition(index: String, `type`: String) extends BulkCompatibleDefinition {
  require(index != null, "index must not be null or empty")
  require(`type` != null, "type must not be null or empty")

  private val _request = new IndexRequest(index, `type`)
  private val _fields = mutable.Buffer[FieldValue]()
  private var _source: Option[DocumentSource] = None
  private var _json: Option[String] = None
  private var _map: Option[DocumentMap] = None

  def build = _source match {
    case Some(src) => _request.source(src.json)
    case None =>
      _json match {
        case Some(json) => _request.source(json)
        case None =>
          _map match {
            case Some(map) => _request.source(map.map.asJava)
            case None => _request.source(_fieldsAsXContent)
          }
      }
  }

  def _fieldsAsXContent: XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()
    _fields.foreach(_.output(source))
    source.endObject()
  }

  def source(json: String): this.type = {
    this._json = Option(json)
    this
  }

  def source[T](t: T)(implicit indexable: Indexable[T]): this.type = {
    this._json = Option(indexable.json(t))
    this
  }

  def id(id: Any): IndexDefinition = {
    _request.id(id.toString)
    this
  }

  def withId(id: Any): IndexDefinition = {
    _request.id(id.toString)
    this
  }

  def opType(opType: IndexRequest.OpType): IndexDefinition = {
    _request.opType(opType)
    this
  }

  def parent(parent: String): IndexDefinition = {
    _request.parent(parent)
    this
  }

  def refresh(refresh: RefreshPolicy): this.type = {
    _request.setRefreshPolicy(refresh)
    this
  }

  def routing(routing: String): IndexDefinition = {
    _request.routing(routing)
    this
  }

  def timestamp(timestamp: String): IndexDefinition = {
    _request.timestamp(timestamp)
    this
  }

  def ttl(ttl: Long): IndexDefinition = {
    _request.ttl(ttl)
    this
  }

  def ttl(duration: FiniteDuration): this.type = {
    _request.ttl(duration.toMillis)
    this
  }

  def update(update: Boolean): IndexDefinition = if (update) opType(OpType.CREATE) else opType(OpType.INDEX)

  def version(version: Long): IndexDefinition = {
    _request.version(version)
    this
  }

  def versionType(versionType: VersionType): IndexDefinition = {
    _request.versionType(versionType)
    this
  }

  def fields(fields: Map[String, Any]): IndexDefinition = {
    _fields ++= FieldsMapper.mapFields(fields)
    this
  }

  def fields(_fields: (String, Any)*): IndexDefinition = fields(_fields.toMap)
  def fields(_fields: Iterable[(String, Any)]): IndexDefinition = fields(_fields.toMap)

  def fieldValues(fields: FieldValue*): IndexDefinition = {
    _fields ++= fields
    this
  }
}
