package com.sksamuel.elastic4s.http.locks

import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.locks.{AcquireGlobalLockDefinition, ReleaseGlobalLockDefinition}
import org.elasticsearch.client.{Response, RestClient}

import scala.concurrent.Future
import scala.util.{Success, Try}

trait LocksImplicits {

  implicit object AcquireGlobalLockHttpExecutable extends HttpExecutable[AcquireGlobalLockDefinition, Boolean] {

    val endpoint = "/fs/lock/global/_create"

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def onResponse(response: Response): Try[Boolean] = {
        Success(response.getStatusLine.getStatusCode == 201)
      }
    }

    override def execute(client: RestClient, request: AcquireGlobalLockDefinition): Future[Response] = {
      client.async("PUT", endpoint, Map.empty)
    }
  }

  implicit object ReleaseGlobalLockHttpExecutable extends HttpExecutable[ReleaseGlobalLockDefinition, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def onResponse(response: Response): Try[Boolean] = {
        Success(response.getStatusLine.getStatusCode == 200)
      }
    }

    override def execute(client: RestClient, request: ReleaseGlobalLockDefinition): Future[Response] = {
      client.async("DELETE", "/fs/lock/global", Map.empty)
    }
  }
}
