package com.sksamuel.elastic4s

import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.Client

import scala.concurrent.{ Future, Promise }

trait Executable[T, R] {

  protected def injectFuture(f: ActionListener[R] => Unit): Future[R] = {
    val p = Promise[R]()
    f(new ActionListener[R] {
      def onFailure(e: Throwable): Unit = p.tryFailure(e)
      def onResponse(response: R): Unit = p.trySuccess(response)
    })
    p.future
  }

  def apply(client: Client, t: T): Future[R]
}
