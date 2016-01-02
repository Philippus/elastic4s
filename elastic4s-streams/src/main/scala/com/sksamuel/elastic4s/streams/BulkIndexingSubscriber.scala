package com.sksamuel.elastic4s.streams

import akka.actor.{Actor, ActorRef, ActorSystem, Cancellable, Props}
import com.sksamuel.elastic4s.{BulkCompatibleDefinition, BulkDefinition, BulkItemResult, BulkResult, ElasticClient, ElasticDsl}
import org.reactivestreams.{Subscriber, Subscription}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

/**
 * An implementation of the reactive API Subscriber.
 * This subscriber will bulk index received elements. The bulk nature means that the elasticsearch
 * index operations are performed as a bulk call, the size of which are controlled by the batchSize param.
 *
 * The received elements must be converted into an elastic4s bulk compatible definition, such as index or delete.
 * This is done by the RequestBuilder.
 *
 * @param client used to connect to the cluster
 * @param builder used to turn elements of T into IndexDefinitions so they can be used in the bulk indexer
 * @param listener a listener which is notified on each acknowledge batch item
 * @param batchSize the number of elements to group together per batch aside from the last batch
 * @param concurrentRequests the number of concurrent batch operations
 * @param refreshAfterOp if the index should be refreshed after each bulk operation
 * @param completionFn a function which is invoked when all sent requests have been acknowledged and the publisher has completed
 * @param errorFn a function which is invoked when there is an error
 * @param flushInterval used to schedule periodic bulk indexing. This can be set to avoid waiting for a complete batch
 *                     for a long period of time. It also is used if the publisher will never complete.
 *                     This ensures that all elements are indexed, even if the last batch size is lower than batch size.
 * @param flushAfter used to schedule an index if no document has been received within the given duration.
 *                   Once an index is performed (either by this flush value or because docs arrived in time)
 *                   the flush after schedule is reset.
 *
 * @tparam T the type of element provided by the publisher this subscriber will subscribe with
 */
class BulkIndexingSubscriber[T] private[streams](client: ElasticClient,
                                                 builder: RequestBuilder[T],
                                                 listener: ResponseListener,
                                                 batchSize: Int,
                                                 concurrentRequests: Int,
                                                 refreshAfterOp: Boolean,
                                                 completionFn: () => Unit,
                                                 errorFn: Throwable => Unit,
                                                 flushInterval: Option[FiniteDuration])
                                                (implicit system: ActorSystem) extends Subscriber[T] {

  private var actor: ActorRef = _

  override def onSubscribe(s: Subscription): Unit = {
    // rule 1.9 https://github.com/reactive-streams/reactive-streams-jvm#2.5
    // when the provided Subscriber is null in which case it MUST throw a java.lang.NullPointerException to the caller
    if (s == null) throw new NullPointerException()
    if (actor == null) {
      actor = system.actorOf(
        Props(new BulkActor(client,
          builder,
          s,
          batchSize,
          concurrentRequests,
          refreshAfterOp,
          listener,
          completionFn,
          errorFn,
          flushInterval))
      )
      s.request(batchSize * concurrentRequests)
    } else {
      // rule 2.5, must cancel subscription as onSubscribe has been invoked twice
      // https://github.com/reactive-streams/reactive-streams-jvm#2.5
      s.cancel()
    }
  }

  override def onNext(t: T): Unit = {
    if (t == null) throw new NullPointerException("On next should not be called until onSubscribe has returned")
    actor ! t
  }

  override def onError(t: Throwable): Unit = {
    if (t == null) throw new NullPointerException()
    actor ! t
  }

  override def onComplete(): Unit = {
    actor ! BulkActor.Completed
  }
}

object BulkActor {
  // signifies that the downstream publisher has completed (NOT that a bulk request has suceeded)
  case object Completed
  case object ForceIndexing
  case class Result(items: Seq[BulkItemResult])
  case object Request
}

class BulkActor[T](client: ElasticClient,
                   builder: RequestBuilder[T],
                   subscription: Subscription,
                   batchSize: Int,
                   concurrentRequests: Int,
                   refreshAfterOp: Boolean,
                   listener: ResponseListener,
                   completionFn: () => Unit,
                   errorFn: Throwable => Unit,
                   flushInterval: Option[FiniteDuration] = None,
                   flushAfter: Option[FiniteDuration] = None) extends Actor {

  import ElasticDsl._
  import context.{dispatcher, system}

  private val buffer = new ArrayBuffer[T]()
  buffer.sizeHint(batchSize)

  private var completed = false

  // documents sent and acknowledged at the elasticsearch cluster level but pending confirmation
  // we need to keep track so we know when we can shutdown
  private var pending: Long = 0l

  // total number of documents requested
  private var requested: Long = 0l

  // Create a scheduler if a flushInterval is provided. This scheduler will be used to force indexing, otherwise
  // we can be stuck at batchSize-1 waiting for the nth message to arrive.
  //
  // It has been suggested we can use ReceiveTimeout here, but one reason we can't is because BulkResult messages,
  // will cause the timeout period to be reset, but they shouldn't interfere with the flush interval.
  private val flushIntervalScheduler: Option[Cancellable] = flushInterval.map { interval =>
    system.scheduler.schedule(interval, interval, self, BulkActor.ForceIndexing)
  }

  // If flushAfter is specified then after each message, a scheduler is created to force indexing if no documents
  // are received within the given duration.
  private var flushAfterScheduler: Option[Cancellable] = None

  private def resetFlushAfterScheduler(): Unit = {
    flushAfterScheduler.map(_.cancel)
    flushAfterScheduler = flushAfter.map { interval =>
      system.scheduler.scheduleOnce(interval, self, BulkActor.ForceIndexing)
    }
  }

  def receive = {
    case t: Throwable =>
      handleError(t)

    case BulkActor.Completed =>
      if (buffer.nonEmpty)
        index()
      completed = true
      shutdownIfAllAcked()

    case BulkActor.Request =>
      subscription.request(batchSize)
      requested = requested + batchSize

    case BulkActor.ForceIndexing =>
      if (buffer.nonEmpty)
        index()

    case BulkActor.Result(items) =>
      pending = pending - items.size
      items.foreach(listener.onAck)
      // need to check if we're completed, because if we are then this might be the last pending ack
      // and if it is, we can shutdown.
      if (completed) shutdownIfAllAcked()

    case t: T =>
      buffer.append(t)
      if (buffer.size == batchSize) {
        index()
      } else {
        resetFlushAfterScheduler()
      }
  }

  // Stops the schedulers if they exist
  override def postStop() = {
    flushIntervalScheduler.map(_.cancel)
    flushAfterScheduler.map(_.cancel)
  }

  private def shutdownIfAllAcked(): Unit = {
    if (pending == 0) {
      completionFn()
      context.stop(self)
    }
  }

  private def handleError(t: Throwable): Unit = {
    // if an error we will cancel the subscription as we cannot for sure handle further elements
    // and the error may be from outside the subscriber
    subscription.cancel()
    errorFn(t)
    buffer.clear()
    context.stop(self)
  }

  private def index(): Unit = {
    pending = pending + buffer.size

    def bulkDef: BulkDefinition = {
      val defs = buffer.map(t => builder.request(t))
      BulkDefinition(defs).refresh(refreshAfterOp)
    }

    // returns just the requests that failed as a new bulk definition
    def retryDef(bulk: BulkDefinition, resp: BulkResult): BulkDefinition = {
      val failureIds = resp.failures.map(_.itemId).toSet
      val failedReqs = bulk.requests.zipWithIndex.filter { case (_, index) => failureIds.contains(index) }.map(_._1)
      BulkDefinition(failedReqs).refresh(refreshAfterOp)
    }

    def send(req: BulkDefinition): Unit = {
      client.execute(req).onComplete {
        case Failure(e) => self ! e
        case Success(resp: BulkResult) =>

          if (resp.hasFailures)
            send(retryDef(req, resp))
          // only once the req has no failures can we safely request another batch from our downstream publisher
          else
            self ! BulkActor.Request

          if (resp.hasSuccesses)
            self ! BulkActor.Result(resp.successes)

        case Success(resp: BulkResult) => self ! resp
      }
    }

    send(bulkDef)
    buffer.clear

    // buffer is now empty so no point keeping a scheduled flush after operation
    flushAfterScheduler.foreach(_.cancel)
    flushAfterScheduler = None
  }
}

/**
 * An implementation of this typeclass must provide a bulk compatible request for the given instance of T.
 * @tparam T the type of elements this provider supports
 */
trait RequestBuilder[T] {
  def request(t: T): BulkCompatibleDefinition
}

/**
 * Notified on each acknowledgement
 */
trait ResponseListener {
  def onAck(resp: BulkItemResult): Unit
}

object ResponseListener {
  def noop = new ResponseListener {
    override def onAck(resp: BulkItemResult): Unit = ()
  }
}
