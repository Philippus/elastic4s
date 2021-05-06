package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.{ElasticRequest, ElasticUrlEncoder, Handler}

trait UserHandlers {
	private val ROLE_BASE_PATH = "/_security/user/"

	implicit object GetUserHandler extends Handler[GetUserRequest, Map[String,GetUserResponse]] {

		override def build(request: GetUserRequest): ElasticRequest = {
			val endpoint = ROLE_BASE_PATH + ElasticUrlEncoder.encodeUrlFragment(request.name)
			ElasticRequest("GET", endpoint)
		}
	}
}
