package com.sksamuel.elastic4s.http

import org.elasticsearch.client.ResponseListener

/*
 *  A typeclass that can be used to convert from a ResponseListener callback into a generic effect type.
 */
trait FromListener[F[_]] {
  def fromListener(f: ResponseListener => Unit): F[HttpResponse]
}

object FromResponseListener {
  def apply[F[_]: FromListener]: FromListener[F] = implicitly[FromListener[F]]
}
