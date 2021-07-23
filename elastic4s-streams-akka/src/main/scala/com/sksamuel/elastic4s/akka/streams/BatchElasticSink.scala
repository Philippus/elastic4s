package com.nt.streaming

import akka.stream.stage.{ GraphStage, GraphStageLogic, InHandler }
import akka.stream.{ Attributes, Inlet, SinkShape }
import com.sksamuel.elastic4s.handlers.bulk.BulkHandlers
import com.sksamuel.elastic4s.requests.bulk.{ BulkCompatibleRequest, BulkRequest, BulkResponse }
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.{ ElasticClient, Executor, Functor, RequestFailure, RequestSuccess, Response }
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

case class SinkSettings(refreshAfterOp: Boolean = false)

class BatchElasticSink[T](client: ElasticClient, settings: SinkSettings)(implicit
    ec: ExecutionContext,
    builder: RequestBuilder[T])
    extends GraphStage[SinkShape[Seq[T]]] {

  private val in: Inlet[Seq[T]] = Inlet.create("ElasticSink.out")
  override val shape: SinkShape[Seq[T]] = SinkShape.of(in)

  private implicit val bulkHandler: BulkHandlers.BulkHandler.type = BulkHandlers.BulkHandler
  private implicit val executor: Executor[Future] = Executor.FutureExecutor
  private implicit val functor: Functor[Future] = Functor.FutureFunctor

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      private val handler: InHandler = new InHandler {
        override def onPush(): Unit = {
          val seq = grab(in)
          index(seq.map(builder.request))
        }
      }

      override def preStart(): Unit = pull(in)

      setHandler(in, handler)

      private def callBack(requests: Seq[BulkCompatibleRequest]) =
        getAsyncCallback[Try[Response[BulkResponse]]] {
          case Failure(t) => failStage(t)
          case Success(resp) =>
            resp match {
              case RequestFailure(_, _, _, error) => failStage(error.asException)
              case RequestSuccess(_, _, _, result) =>
                val failedRequests = result.failures.map { item =>
                  requests(item.itemId)
                }
                if (failedRequests.nonEmpty)
                  index(failedRequests)
                else
                  pull(in)
            }
        }

      private def index(requests: Seq[BulkCompatibleRequest]): Unit = {

        val policy = if (settings.refreshAfterOp) RefreshPolicy.Immediate else RefreshPolicy.NONE
        val f = client.execute {
          BulkRequest(requests).refresh(policy)
        }
        f.onComplete(callBack(requests).invoke)

      }

    }
}

/**
  * An implementation of this typeclass must provide a bulk compatible request for the given instance of T.
  * The bulk compatible request will then be sent to elastic.
  *
  * A bulk compatible request can be either an index, update, or delete.
  *
  * @tparam T the type of elements this builder supports
  */
trait RequestBuilder[T] {
  def request(t: T): BulkCompatibleRequest
}
