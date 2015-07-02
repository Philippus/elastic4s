package com.sksamuel.elastic4s.streams

import com.sksamuel.elastic4s.RichSearchHit
import org.reactivestreams.{Publisher, Subscription, Subscriber}

class ElasticPublisher extends Publisher[RichSearchHit] {
  override def subscribe(s: Subscriber[_ >: RichSearchHit]): Unit = ???
}

class ElasticSubscriber[T] extends Subscriber[T] {
  override def onComplete(): Unit = ???
  override def onError(t: Throwable): Unit = ???
  override def onSubscribe(s: Subscription): Unit = ???
  override def onNext(t: T): Unit = ???
}