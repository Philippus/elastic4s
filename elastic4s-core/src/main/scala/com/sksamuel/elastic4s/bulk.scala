package com.sksamuel.elastic4s

import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.bulk.BulkItemResponse.Failure
import org.elasticsearch.action.bulk.{BulkItemResponse, BulkRequest, BulkResponse}
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait BulkCompatibleDefinition

trait BulkDsl {
  this: IndexDsl =>

  def bulk(requests: Iterable[BulkCompatibleDefinition]): BulkDefinition = new BulkDefinition(requests.toSeq)
  def bulk(requests: BulkCompatibleDefinition*): BulkDefinition = bulk(requests)

  implicit object BulkDefinitionExecutable
    extends Executable[BulkDefinition, BulkResponse, BulkResult] {
    override def apply(c: Client, t: BulkDefinition): Future[BulkResult] = {
      injectFutureAndMap(c.bulk(t.build, _))(BulkResult.apply)
    }
  }
}

case class BulkResult(original: BulkResponse) {

  @deprecated("use new scala idiomatic methods, or call .original to get the java response", "2.0")
  def getItems = original.getItems

  @deprecated("use new scala idiomatic methods, or call .original to get the java response", "2.0")
  def getTook: TimeValue = original.getTook

  @deprecated("use new scala idiomatic methods, or call .original to get the java response", "2.0")
  def iterator = original.iterator()

  @deprecated("use new scala idiomatic methods, or call .original to get the java response", "2.0")
  def getTookInMillis: Long = original.getTookInMillis

  @deprecated("use new scala idiomatic methods, or call .original to get the java response", "2.0")
  def buildFailureMessage: String = original.buildFailureMessage


  import scala.concurrent.duration._

  def failureMessage: String = original.buildFailureMessage
  def took: FiniteDuration = original.getTook.millis.millis
  def hasSuccesses: Boolean = !hasFailures
  def hasFailures: Boolean = original.hasFailures
  def items: Seq[BulkItemResult] = original.getItems.map(BulkItemResult.apply)
  def failures: Seq[BulkItemResult] = items.filter(_.isFailure)
  def successes: Seq[BulkItemResult] = items.filterNot(_.isFailure)
}

case class BulkItemResult(original: BulkItemResponse) {
  def failure: Failure = original.getFailure
  def failureMessage = original.getFailureMessage
  def id = original.getId
  def index = original.getIndex
  def itemId = original.getItemId
  def opType = original.getOpType
  def deleteResponse: Option[DeleteResponse] = original.getResponse match {
    case d: DeleteResponse => Some(d)
    case _ => None
  }
  def indexResult: Option[IndexResult] = original.getResponse match {
    case i: IndexResponse => Some(IndexResult(i))
    case _ => None
  }
  def `type` = original.getType
  def version = original.getVersion
  def isFailure: Boolean = original.isFailed
}

case class BulkDefinition(requests: Seq[BulkCompatibleDefinition]) {

  def build = _builder

  def timeout(value: String): this.type = {
    _builder.timeout(value)
    this
  }

  def timeout(value: TimeValue): this.type = {
    _builder.timeout(value)
    this
  }

  def timeout(duration: Duration): this.type = {
    _builder.timeout(TimeValue.timeValueMillis(duration.toMillis))
    this
  }

  def refresh(refresh: Boolean): this.type = {
    _builder.refresh(refresh)
    this
  }

  def consistencyLevel(level: WriteConsistencyLevel): this.type = {
    _builder.consistencyLevel(level)
    this
  }

  private val _builder = new BulkRequest()
  requests.foreach {
    case index: IndexDefinition => _builder.add(index.build)
    case delete: DeleteByIdDefinition => _builder.add(delete.build)
    case update: UpdateDefinition => _builder.add(update.build)
    case register: RegisterDefinition => _builder.add(register.build)
  }
}