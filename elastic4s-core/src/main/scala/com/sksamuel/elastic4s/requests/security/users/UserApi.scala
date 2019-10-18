package com.sksamuel.elastic4s.requests.security.users

trait UserApi {
	// Returns the named user
	def getUser(name: String) = GetUserRequest(name)

	// Returns all roles
	def getUsers() = GetUserRequest("")
}