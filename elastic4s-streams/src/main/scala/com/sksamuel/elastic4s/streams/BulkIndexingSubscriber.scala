package com.sksamuel.elastic4s.streams

import akka.actor.{Actor, ActorRef, ActorSystem, Props, ReceiveTimeout}
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
 * @param flushAfter used to force bulk indexing after the specified inactivity timeout. Use it when the publisher will never complete.
 *                     This ensures that all elements are indexed, even if the last batch size is lower than batch size
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
                                                 flushAfter: Option[FiniteDuration])
                                                (implicit system: ActorSystem) extends Subscriber[T] {

  private var actor: ActorRef = _

  override def onSubscribe(s: Subscription): Unit = {
    // rule 1.9 https://github.com/reactive-streams/reactive-streams-jvm#2.5
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
          flushAfter))
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
  case object Completed
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
                   flushAfter: Option[FiniteDuration] = None) extends Actor {

  import ElasticDsl._
  import context.dispatcher

  private val buffer = new ArrayBuffer[T]()
  buffer.sizeHint(batchSize)

  private var completed = false

  // total number of elements sent and acknowledge at the es cluster level
  private var pending: Long = 0l

  // Schedule Akka's receive timeout if a flushAfter value is provided. This scheduler
  // will be used to force indexing, otherwise we can be stuck at batchSize-1 waiting
  // for the nth message for ages.
  flushAfter.foreach { timeout =>
    context.setReceiveTimeout(timeout)
  }

  def receive = {
    case t: Throwable =>
      handleError(t)

    case BulkActor.Completed =>
      if (buffer.nonEmpty)
        index()
      completed = true
      shutdownIfAllAcked()

    case ReceiveTimeout =>
      if (pending == 0 && buffer.nonEmpty)
        index()

    case r: BulkResult =>
      pending = pending - r.successes.size
      r.items.foreach(listener.onAck)
      // need to check if we're completed, because if we are then this might be the last pending ack
      // and if it is, we can shutdown. Otherwise w can set another batch going.
      if (completed) shutdownIfAllAcked()
      else subscription.request(batchSize)

    case t: T =>
      buffer.append(t)
      if (buffer.size == batchSize)
        index()
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
    def send(req: BulkDefinition): Unit = {
      client.execute(req).onComplete {
        case Failure(e) => self ! e
        case Success(resp) if resp.hasFailures => send(req)
        case Success(resp) => self ! resp
      }
    }
    val defs = buffer.map(t => builder.request(t))
    val req = bulk(defs).refresh(refreshAfterOp)
    send(req)
    buffer.clear
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
