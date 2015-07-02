package com.sksamuel.elastic4s.streams

import org.reactivestreams.{Subscription, Subscriber}

class ElasticSubscriber[T] extends Subscriber[T] {
  override def onComplete(): Unit = ???
  override def onError(t: Throwable): Unit = ???
  override def onSubscribe(s: Subscription): Unit = ???
  override def onNext(t: T): Unit = ???
}
