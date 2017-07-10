package com.sksamuel.elastic4s.http.locks

import com.sksamuel.elastic4s.http.{HttpExecutable, HttpRequestClient, HttpResponse, ResponseHandler}
import com.sksamuel.elastic4s.locks.{AcquireGlobalLockDefinition, ReleaseGlobalLockDefinition}

import scala.concurrent.Future
import scala.util.{Success, Try}

trait LocksImplicits {

  implicit object AcquireGlobalLockHttpExecutable extends HttpExecutable[AcquireGlobalLockDefinition, Boolean] {

    val endpoint = "/fs/lock/global/_create"

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse): Try[Boolean] = {
        Success(response.statusCode == 201)
      }
    }

    override def execute(client: HttpRequestClient, request: AcquireGlobalLockDefinition): Future[HttpResponse] = {
      client.async("PUT", endpoint, Map.empty)
    }
  }

  implicit object ReleaseGlobalLockHttpExecutable extends HttpExecutable[ReleaseGlobalLockDefinition, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse): Try[Boolean] = {
        Success(response.statusCode == 200)
      }
    }

    override def execute(client: HttpRequestClient, request: ReleaseGlobalLockDefinition): Future[HttpResponse] = {
      client.async("DELETE", "/fs/lock/global", Map.empty)
    }
  }
}
