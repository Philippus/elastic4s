package com.sksamuel.elastic4s.requests.security.roles.admin

import com.sksamuel.elastic4s.requests.security.roles.{CreateOrUpdateRoleContentBuilder, CreateOrUpdateRoleRequest, CreateRole, CreateRoleResponse, DeleteRoleRequest, UpdateRole}
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, ElasticUrlEncoder, Handler, HttpEntity, HttpResponse, ResponseHandler}

trait RoleAdminHandlers {
	private val ROLE_BASE_PATH = "/_security/role/"

	implicit object CreateOrUpdateRoleHandler extends Handler[CreateOrUpdateRoleRequest, CreateRoleResponse] {
		override def responseHandler: ResponseHandler[CreateRoleResponse] = new ResponseHandler[CreateRoleResponse] {
			override def handle(response: HttpResponse): Either[ElasticError, CreateRoleResponse] = response.statusCode match {
				case 200 | 201 => Right(ResponseHandler.fromResponse[CreateRoleResponse](response))
				case 400 | 500 => Left(ElasticError.parse(response))
				case _ 				 => sys.error(response.toString)
			}
		}

		override def build(request: CreateOrUpdateRoleRequest): ElasticRequest = {
			val endpoint = ROLE_BASE_PATH + ElasticUrlEncoder.encodeUrlFragment(request.name)

			val body = CreateOrUpdateRoleContentBuilder(request).string()
			val entity = HttpEntity(body, "application/json")
			val method = request.action match {
				case CreateRole => "POST"
				case UpdateRole => "PUT"
			}

			ElasticRequest(method, endpoint, entity)
		}
	}

	implicit object DeleteRoleHandler extends Handler[DeleteRoleRequest, DeleteRoleResponse] {
		override def responseHandler: ResponseHandler[DeleteRoleResponse] = new ResponseHandler[DeleteRoleResponse] {
			override def handle(response: HttpResponse): Either[ElasticError, DeleteRoleResponse] = response.statusCode match {
				case 200 | 201 | 404 => Right(ResponseHandler.fromResponse[DeleteRoleResponse](response))
				case 400 | 500 			 => Left(ElasticError.parse(response))
				case _ 							 => sys.error(response.toString)
			}
		}

		override def build(request: DeleteRoleRequest): ElasticRequest = {
			val endpoint = ROLE_BASE_PATH + ElasticUrlEncoder.encodeUrlFragment(request.name)
			ElasticRequest("DELETE", endpoint)
		}
	}

	implicit object ClearRolesCacheHandler extends Handler[ClearRolesCacheRequest, ClearRolesCacheResponse] {
		override def build(request: ClearRolesCacheRequest): ElasticRequest = {
			val endpoint = ROLE_BASE_PATH + ElasticUrlEncoder.encodeUrlFragment(request.name) + "/_clear_cache"
			ElasticRequest("POST", endpoint)
		}

		override def responseHandler: ResponseHandler[ClearRolesCacheResponse] = new ResponseHandler[ClearRolesCacheResponse] {
			override def handle(response: HttpResponse): Either[ElasticError, ClearRolesCacheResponse] = response.statusCode match {
				case 200 | 201 => Right(ResponseHandler.fromResponse[ClearRolesCacheResponse](response))
				case 400 | 500 => Left(ElasticError.parse(response))
				case _ 				 => sys.error(response.toString)
			}
		}
	}
}
