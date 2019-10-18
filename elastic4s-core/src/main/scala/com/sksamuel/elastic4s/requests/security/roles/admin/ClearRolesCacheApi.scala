package com.sksamuel.elastic4s.requests.security.roles.admin

trait ClearRolesCacheApi {
	def clearRolesCache(name: String) = ClearRolesCacheRequest(name)
}