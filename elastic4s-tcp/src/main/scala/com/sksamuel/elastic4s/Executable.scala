package com.sksamuel.elastic4s

import org.elasticsearch.action.{ActionFuture, ActionListener, ListenableActionFuture}
import org.elasticsearch.client.Client

import scala.concurrent.{Future, Promise}

/**
* Typeclass to execute a search for a given type of Elasticsearch request.
*
* @tparam T is the elastic4s request definition type
* @tparam R is the type returned by the Elasticsearch java client for this type.
* @tparam Q is the type returned by Elastic4s to the user for this request type.
*           If the particular implementation of this typeclass doesn't return a pimped/scala/rich response type,
*           then R and Q will be the same.
*/
trait Executable[T, R, Q] {

  protected def injectFutureAndMap(f: ActionListener[R] => Any)(mapFn: R => Q): Future[Q] = {
    val p = Promise[Q]()
    f(new ActionListener[R] {
      def onFailure(e: Exception): Unit = p.tryFailure(e)
      def onResponse(resp: R): Unit = p.trySuccess(mapFn(resp))
    })
    p.future
  }

  protected def injectFuture(f: ActionListener[R] => Any): Future[R] = {
    val p = Promise[R]()
    f(new ActionListener[R] {
      def onFailure(e: Exception): Unit = p.tryFailure(e)
      def onResponse(resp: R): Unit = p.trySuccess(resp)
    })
    p.future
  }

  protected def injectFuture(future: ListenableActionFuture[R]): Future[R] = {
    val p = Promise[R]()
    future.addListener(new ActionListener[R] {
      def onFailure(e: Exception): Unit = p.tryFailure(e)
      def onResponse(resp: R): Unit = p.trySuccess(resp)
    })
    p.future
  }

  def apply(client: Client, t: T): Future[Q]
}
