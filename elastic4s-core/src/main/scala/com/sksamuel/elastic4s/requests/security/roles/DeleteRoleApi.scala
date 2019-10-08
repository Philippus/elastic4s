package com.sksamuel.elastic4s.requests.security.roles

trait DeleteRoleApi {
	def deleteRole(name: String): DeleteRoleRequest = DeleteRoleRequest(name)
}