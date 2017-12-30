package com.sksamuel.elastic4s.http.locks

import cats.Functor
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.locks.{AcquireGlobalLock, ReleaseGlobalLock}

trait LocksImplicits {

  implicit object AcquireGlobalLockHttpExecutable extends HttpExecutable[AcquireGlobalLock, Boolean] {

    val endpoint = "/fs/lock/global/_create"

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse) = Right(response.statusCode == 201)
    }

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: AcquireGlobalLock): F[HttpResponse] = {
      client.async("PUT", endpoint, Map.empty)
    }
  }

  implicit object ReleaseGlobalLockHttpExecutable extends HttpExecutable[ReleaseGlobalLock, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse) = Right(response.statusCode == 200)
    }

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: ReleaseGlobalLock): F[HttpResponse] = {
      client.async("DELETE", "/fs/lock/global", Map.empty)
    }
  }
}
