package com.sksamuel.elastic4s.akka.streams

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import com.sksamuel.elastic4s.requests.bulk
import com.sksamuel.elastic4s.requests.bulk.{BulkCompatibleRequest, BulkHandlers, BulkRequest, BulkResponse}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.{ElasticClient, Executor, Functor, RequestFailure, RequestSuccess, Response}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class SinkSettings(bufferSize: Int, refreshAfterOp: Boolean = false)

class ElasticSink[T](client: ElasticClient, settings: SinkSettings)
                    (implicit ec: ExecutionContext, builder: RequestBuilder[T]) extends GraphStage[SinkShape[T]] {

  private val in: Inlet[T] = Inlet.create("ElasticSink.out")
  override val shape: SinkShape[T] = SinkShape.of(in)

  private implicit val bulkHandler: bulk.BulkHandlers.BulkHandler.type = BulkHandlers.BulkHandler
  private implicit val executor: Executor[Future] = Executor.FutureExecutor
  private implicit val functor: Functor[Future] = Functor.FutureFunctor

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) with InHandler {

    private val buffer = scala.collection.mutable.ListBuffer.empty[BulkCompatibleRequest]
    private val pending = scala.collection.mutable.Map.empty[Int, BulkCompatibleRequest]

    private val handler = getAsyncCallback[Try[Response[BulkResponse]]] {
      case Failure(t) => failStage(t)
      case Success(resp) => resp match {
        case RequestFailure(_, _, _, error) => failStage(error.asException)
        case RequestSuccess(_, _, _, result) =>
          result.successes.foreach { item => pending.remove(item.itemId) }
          result.failures.flatMap { item =>
            pending.get(item.itemId)
          }.foreach { item => buffer.append(item) }
          if (buffer.size == settings.bufferSize) {
            index()
          }
      }
    }

    private def index(): Unit = {

      val policy = if (settings.refreshAfterOp) RefreshPolicy.Immediate else RefreshPolicy.NONE
      val f = client.execute {
        BulkRequest(buffer.toList).refresh(policy)
      }
      f.onComplete(handler.invoke)

      //   sent = sent + buffer.size
      //    self ! BulkActor.Send(bulkDef, buffer.toList, config.maxAttempts)

      buffer.clear

      // buffer is now empty so no point keeping a scheduled flush after operation
      //  flushAfterScheduler.foreach(_.cancel)
      //  flushAfterScheduler = None
    }

    override def onPush(): Unit = {
      val t = grab(in)
      buffer.append(builder.request(t))
      if (buffer.size == settings.bufferSize) {
        index()
      }
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
