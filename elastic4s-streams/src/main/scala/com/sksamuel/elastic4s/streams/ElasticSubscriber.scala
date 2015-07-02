package com.sksamuel.elastic4s.streams

class ElasticSubscriber[T] extends Subscriber[T] {
  override def onComplete(): Unit = ???
  override def onError(t: Throwable): Unit = ???
  override def onSubscribe(s: Subscription): Unit = ???
  override def onNext(t: T): Unit = ???
}
