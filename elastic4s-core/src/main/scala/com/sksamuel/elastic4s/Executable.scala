package com.sksamuel.elastic4s

import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.Client

import scala.concurrent.{Future, Promise}

trait Executable[T, R, Q] {

  protected def injectFutureAndMap(f: ActionListener[R] => Any, mapFn: R => Q): Future[Q] = {
    val p = Promise[Q]()
    f(new ActionListener[R] {
      def onFailure(e: Throwable): Unit = p.tryFailure(e)
      def onResponse(resp: R): Unit = p.trySuccess(mapFn(resp))
    })
    p.future
  }

  protected def injectFuture(f: ActionListener[R] => Any): Future[R] = {
    val p = Promise[R]()
    f(new ActionListener[R] {
      def onFailure(e: Throwable): Unit = p.tryFailure(e)
      def onResponse(resp: R): Unit = p.trySuccess(resp)
    })
    p.future
  }

  def apply(client: Client, t: T): Future[Q]
}
