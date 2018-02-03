package com.sksamuel.elastic4s.http.locks

import com.sksamuel.elastic4s.http.{HttpExecutable, HttpClient, HttpResponse, ResponseHandler}
import com.sksamuel.elastic4s.locks.{AcquireGlobalLock, ReleaseGlobalLock}

import scala.concurrent.Future

trait LocksImplicits {

  implicit object AcquireGlobalLockHttpExecutable extends HttpExecutable[AcquireGlobalLock, Boolean] {

    val endpoint = "/fs/lock/global/_create"

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse) = Right(response.statusCode == 201)
    }

    override def execute(client: HttpClient, request: AcquireGlobalLock): Future[HttpResponse] =
      client.async("PUT", endpoint, Map.empty)
  }

  implicit object ReleaseGlobalLockHttpExecutable extends HttpExecutable[ReleaseGlobalLock, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse) = Right(response.statusCode == 200)
    }

    override def execute(client: HttpClient, request: ReleaseGlobalLock): Future[HttpResponse] =
      client.async("DELETE", "/fs/lock/global", Map.empty)
  }
}
