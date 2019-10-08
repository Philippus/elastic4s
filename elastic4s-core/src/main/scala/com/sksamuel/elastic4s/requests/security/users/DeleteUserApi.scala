package com.sksamuel.elastic4s.requests.security.users

trait DeleteUserApi {
	def deleteUser(name: String): DeleteUserRequest = DeleteUserRequest(name)
}