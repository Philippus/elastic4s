package com.sksamuel.elastic4s.handlers.security.users

import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.requests.security.users.admin.{ChangePasswordRequest, DeleteUserResponse, DisableUserRequest, EnableUserRequest}
import com.sksamuel.elastic4s.requests.security.users.{CreateOrUpdateUserRequest, CreateUser, CreateUserResponse, DeleteUserRequest, UpdateUser}
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler}

import java.net.URLEncoder

trait UserAdminHandlers {
  private val USER_BASE_PATH = "/_security/user/"

  implicit object CreateOrUpdateUserHandler extends Handler[CreateOrUpdateUserRequest, CreateUserResponse] {
    override def responseHandler: ResponseHandler[CreateUserResponse] = new ResponseHandler[CreateUserResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, CreateUserResponse] = response.statusCode match {
        case 200 | 201 => Right(ResponseHandler.fromResponse[CreateUserResponse](response))
        case 400 | 500 => Left(ElasticErrorParser.parse(response))
        case _ => sys.error(response.toString)
      }
    }

    override def build(request: CreateOrUpdateUserRequest): ElasticRequest = {
      val endpoint = USER_BASE_PATH + URLEncoder.encode(request.name, "UTF-8")

      val body = CreateOrUpdateUserContentBuilder(request).string()
      val entity = HttpEntity(body, "application/json")
      val method = request.action match {
        case CreateUser => "POST"
        case UpdateUser => "PUT"
      }

      ElasticRequest("POST", endpoint, entity)
    }
  }

  implicit object ChangePasswordHandler extends Handler[ChangePasswordRequest, Any] {
    override def build(request: ChangePasswordRequest): ElasticRequest = {
      val endpoint = request.name match {
        case Some(n) => USER_BASE_PATH + URLEncoder.encode(n, "UTF-8") + "/_password"
        case None => USER_BASE_PATH + "_password"
      }
      val body = ChangePasswordContentBuilder(request).string()
      val entity = HttpEntity(body, "application/json")
      ElasticRequest("POST", endpoint, entity)
    }
  }

  implicit object DeleteUserHandler extends Handler[DeleteUserRequest, DeleteUserResponse] {
    override def responseHandler: ResponseHandler[DeleteUserResponse] = new ResponseHandler[DeleteUserResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, DeleteUserResponse] = response.statusCode match {
        case 200 | 201 | 404 => Right(ResponseHandler.fromResponse[DeleteUserResponse](response))
        case 400 | 500 => Left(ElasticErrorParser.parse(response))
        case _ => sys.error(response.toString)
      }
    }

    override def build(request: DeleteUserRequest): ElasticRequest = {
      val endpoint = USER_BASE_PATH + URLEncoder.encode(request.name, "UTF-8")
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object DisableUserHandler extends Handler[DisableUserRequest, Any] {
    override def build(request: DisableUserRequest): ElasticRequest = {
      val endpoint = USER_BASE_PATH + URLEncoder.encode(request.name, "UTF-8") + "/_disable"
      ElasticRequest("PUT", endpoint)
    }
  }

  implicit object EnableUserHandler extends Handler[EnableUserRequest, Any] {
    override def build(request: EnableUserRequest): ElasticRequest = {
      val endpoint = USER_BASE_PATH + URLEncoder.encode(request.name, "UTF-8") + "/_enable"
      ElasticRequest("PUT", endpoint)
    }
  }
}
