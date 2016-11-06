package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.indexes.{IndexDefinition, IndexDsl, IndexResult}
import com.sksamuel.elastic4s.update.UpdateDefinition
import org.elasticsearch.action.bulk.BulkItemResponse.Failure
import org.elasticsearch.action.bulk.{BulkItemResponse, BulkRequest, BulkResponse}
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.implicitConversions

trait BulkCompatibleDefinition

trait BulkDsl {
  this: IndexDsl =>

  def bulk(requests: Iterable[BulkCompatibleDefinition]): BulkDefinition = BulkDefinition(requests.toSeq)
  def bulk(requests: BulkCompatibleDefinition*): BulkDefinition = bulk(requests)

  implicit object BulkDefinitionExecutable
    extends Executable[BulkDefinition, BulkResponse, BulkResult] {
    override def apply(c: Client, t: BulkDefinition): Future[BulkResult] = {
      injectFutureAndMap(c.bulk(t.build, _))(BulkResult.apply)
    }
  }
}

case class BulkResult(original: BulkResponse) {

  import scala.concurrent.duration._

  def failureMessage: String = original.buildFailureMessage
  def failureMessageOpt: Option[String] = Option(failureMessage)

  def took: FiniteDuration = original.getTook.millis.millis
  def hasFailures: Boolean = original.getItems.exists(_.isFailed)
  def hasSuccesses: Boolean = original.getItems.exists(!_.isFailed)
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

  @deprecated("use toDeleteResult", "5.0.0")
  def deleteResponse: Option[DeleteResponse] = original.getResponse match {
    case d: DeleteResponse => Some(d)
    case _ => None
  }

  @deprecated("use toIndexResult", "5.0.0")
  def indexResult: Option[IndexResult] = toIndexResult
  def toIndexResult: Option[IndexResult] = original.getResponse match {
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

  def refresh(refresh: RefreshPolicy): this.type = {
    _builder.setRefreshPolicy(refresh)
    this
  }

  private val _builder = new BulkRequest()
  requests.foreach {
    case index: IndexDefinition => _builder.add(index.build)
    case delete: DeleteByIdDefinition => _builder.add(delete.build)
    case update: UpdateDefinition => _builder.add(update.build)
  }
}
