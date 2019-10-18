package com.sksamuel.elastic4s.requests.security.users

import java.net.URLEncoder
import com.sksamuel.elastic4s.{Handler, ElasticRequest, ResponseHandler}

trait UserHandlers {
	private val ROLE_BASE_PATH = "/_security/user/"

	implicit object GetUserHandler extends Handler[GetUserRequest, Map[String,GetUserResponse]] {

		override def build(request: GetUserRequest): ElasticRequest = {
			val endpoint = ROLE_BASE_PATH + URLEncoder.encode(request.name, "UTF-8")
			ElasticRequest("GET", endpoint)
		}
	}
}