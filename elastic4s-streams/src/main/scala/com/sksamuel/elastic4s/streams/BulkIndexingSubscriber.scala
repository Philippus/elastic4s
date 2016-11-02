package com.sksamuel.elastic4s.streams

import akka.actor._
import com.sksamuel.elastic4s.{BulkCompatibleDefinition, BulkDefinition, BulkItemResult, BulkResult, ElasticClient, ElasticDsl2$}
import org.reactivestreams.{Subscriber, Subscription}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
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
 *
 * @tparam T the type of element provided by the publisher this subscriber will subscribe with
 */
class BulkIndexingSubscriber[T] private[streams](client: ElasticClient,
                                                 builder: RequestBuilder[T],
                                                 config: SubscriberConfig)
                                                (implicit actorRefFactory: ActorRefFactory) extends Subscriber[T] {

  private var actor: ActorRef = _

  override def onSubscribe(sub: Subscription): Unit = {
    // rule 1.9 https://github.com/reactive-streams/reactive-streams-jvm#2.5
    // when the provided Subscriber is null in which case it MUST throw a java.lang.NullPointerException to the caller
    if (sub == null) throw new NullPointerException()
    if (actor == null) {
      actor = actorRefFactory.actorOf(Props(new BulkActor(client, sub, builder, config)))
    } else {
      // rule 2.5, must cancel subscription if onSubscribe has been invoked twice
      // https://github.com/reactive-streams/reactive-streams-jvm#2.5
      sub.cancel()
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

  def close(): Unit = {
    actor ! PoisonPill
  }
}


object BulkActor {

  // signifies that the downstream publisher has completed (NOT that a bulk request has suceeded)
  case object Completed

  case object ForceIndexing

  case class Result(items: Seq[BulkItemResult])

  case class FailedResult(items: Seq[BulkItemResult])

  case class Request(n: Int)

  case class Send(req: BulkDefinition, attempts: Int)

}


class BulkActor[T](client: ElasticClient,
                   subscription: Subscription,
                   builder: RequestBuilder[T],
                   config: SubscriberConfig) extends Actor {

  import ElasticDsl2._
  import context.{dispatcher, system}

  private val buffer = new ArrayBuffer[T]()
  buffer.sizeHint(config.batchSize)

  private var completed = false

  // total number of documents requested from our publisher
  private var requested: Long = 0l

  // total number of documents acknowledged at the elasticsearch cluster level but pending confirmation of index
  private var sent: Long = 0l

  // total number of documents confirmed as successful
  private var confirmed: Long = 0l

  // total number of documents that failed the retry attempts and are ignored
  private var failed: Long = 0l

  // Create a scheduler if a flushInterval is provided. This scheduler will be used to force indexing, otherwise
  // we can be stuck at batchSize-1 waiting for the nth message to arrive.
  //
  // It has been suggested we can use ReceiveTimeout here, but one reason we can't is because BulkResult messages,
  // will cause the timeout period to be reset, but they shouldn't interfere with the flush interval.
  private val flushIntervalScheduler: Option[Cancellable] = config.flushInterval.map { interval =>
    system.scheduler.schedule(interval, interval, self, BulkActor.ForceIndexing)
  }

  // If flushAfter is specified then after each message, a scheduler is created to force indexing if no documents
  // are received within the given duration.
  private var flushAfterScheduler: Option[Cancellable] = None

  private def resetFlushAfterScheduler(): Unit = {
    flushAfterScheduler.foreach(_.cancel)
    flushAfterScheduler = config.flushAfter.map { interval =>
      system.scheduler.scheduleOnce(interval, self, BulkActor.ForceIndexing)
    }
  }

  // requests our initial starting batches, we can request them all at once, and then just request a new batch
  // each time we complete a batch
  override def preStart(): Unit = {
    self ! BulkActor.Request(config.batchSize * config.concurrentRequests)
  }

  def receive = {
    case t: Throwable => handleError(t)

    case BulkActor.Completed =>
      // since we are completed at the publisher level, we should send all remaining documents because a complete
      // batch cannot happen now
      if (buffer.nonEmpty)
        index()
      completed = true
      shutdownIfAllConfirmed()

    case BulkActor.Request(n) =>
      subscription.request(n)
      requested = requested + n

    case BulkActor.Send(req, attempts) => send(req, attempts)

    case BulkActor.ForceIndexing =>
      if (buffer.nonEmpty)
        index()

    case BulkActor.Result(items) =>
      confirmed = confirmed + items.size
      items.foreach(config.listener.onAck)
      checkCompleteOrRequestNext(items.size)

    case BulkActor.FailedResult(items) =>
      failed = failed + items.size
      items.foreach(config.listener.onFailure)
      checkCompleteOrRequestNext(items.size)

    case t: T =>
      buffer.append(t)
      if (buffer.size == config.batchSize) {
        index()
      } else {
        resetFlushAfterScheduler()
      }
  }

  // need to check if we're completed, because if we are then this might be the last pending conf
  // and if it is, we can shutdown.
  private def checkCompleteOrRequestNext(n: Int): Unit = {
    if (completed) shutdownIfAllConfirmed()
    else self ! BulkActor.Request(n)
  }

  // Stops the schedulers if they exist
  override def postStop() = {
    flushIntervalScheduler.map(_.cancel)
    flushAfterScheduler.map(_.cancel)
    config.completionFn()
  }

  private def shutdownIfAllConfirmed(): Unit = {
    if (confirmed + failed == sent) {
      context.stop(self)
    }
  }

  private def send(req: BulkDefinition, attempts: Int): Unit = {

    // returns just requests that failed as a new bulk definition
    def retryDef(resp: BulkResult): BulkDefinition = {
      val failureIds = resp.failures.map(_.itemId).toSet
      val failedReqs = req.requests.zipWithIndex.filter { case (_, index) => failureIds.contains(index) }.map(_._1)
      BulkDefinition(failedReqs).refresh(config.refreshAfterOp)
    }

    client.execute(req).onComplete {
      case Failure(e) => self ! e
      case Success(resp: BulkResult) =>

        if (resp.hasFailures) {
          // all failures need to be resent, if retries left, but only after we wait for the failureWait period to
          // avoid flooding the cluster
          if (attempts > 0)
            system.scheduler.scheduleOnce(config.failureWait, self, BulkActor.Send(retryDef(resp), attempts - 1))
          else
            self ! BulkActor.FailedResult(resp.failures)
        }

        if (resp.hasSuccesses)
          self ! BulkActor.Result(resp.successes)
    }
  }

  private def handleError(t: Throwable): Unit = {
    // if an error we will cancel the subscription as we cannot for sure handle further elements
    // and the error may be from outside the subscriber
    subscription.cancel()
    config.errorFn(t)
    buffer.clear()
    context.stop(self)
  }

  private def index(): Unit = {

    def bulkDef: BulkDefinition = {
      val defs = buffer.map(t => builder.request(t))
      BulkDefinition(defs).refresh(config.refreshAfterOp)
    }

    sent = sent + buffer.size
    self ! BulkActor.Send(bulkDef, config.maxAttempts)

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
  def onFailure(resp: BulkItemResult): Unit = ()
}


object ResponseListener {
  def noop = new ResponseListener {
    override def onAck(resp: BulkItemResult): Unit = ()
  }
}


/**
 * @param listener a listener which is notified on each acknowledge batch item
 * @param batchSize the number of elements to group together per batch aside from the last batch
 * @param concurrentRequests the number of concurrent batch operations
 * @param refreshAfterOp if the index should be refreshed after each bulk operation
 * @param completionFn a function which is invoked when all sent requests have been acknowledged and the publisher has completed
 * @param errorFn a function which is invoked when there is an error
 * @param failureWait the timeout before re-trying failed requests. Usually a failed request is elasticsearch's way of
 *                    indicating backpressure, so this parameter determines how long to wait between requests.
 * @param maxAttempts the max number of times to try a request. If it fails too many times it probably isn't back pressure
 *                    but an error with the document.
 * @param flushInterval used to schedule periodic bulk indexing. This can be set to avoid waiting for a complete batch
 *                      for a long period of time. It also is used if the publisher will never complete.
 *                      This ensures that all elements are indexed, even if the last batch size is lower than batch size.
 * @param flushAfter used to schedule an index if no document has been received within the given duration.
 *                   Once an index is performed (either by this flush value or because docs arrived in time)
 *                   the flush after schedule is reset.
 **/
case class SubscriberConfig(batchSize: Int = 100,
                            concurrentRequests: Int = 5,
                            refreshAfterOp: Boolean = false,
                            listener: ResponseListener = ResponseListener.noop,
                            completionFn: () => Unit = () => (),
                            errorFn: Throwable => Unit = e => (),
                            failureWait: FiniteDuration = 2.seconds,
                            maxAttempts: Int = 5,
                            flushInterval: Option[FiniteDuration] = None,
                            flushAfter: Option[FiniteDuration] = None)
