package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.{ElasticRequest, ElasticUrlEncoder, Handler}

trait RoleHandlers {
	private val ROLE_BASE_PATH = "/_security/role/"

	implicit object GetRoleHandler extends Handler[GetRoleRequest, Map[String,GetRoleResponse]] {

		override def build(request: GetRoleRequest): ElasticRequest = {
			val endpoint = ROLE_BASE_PATH + ElasticUrlEncoder.encodeUrlFragment(request.name)
			ElasticRequest("GET", endpoint)
		}
	}
}
