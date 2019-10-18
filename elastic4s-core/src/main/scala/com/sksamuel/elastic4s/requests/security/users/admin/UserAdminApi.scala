package com.sksamuel.elastic4s.requests.security.users.admin

trait UserAdminApi {
	// Changes password for a specified user
	def changePassword(name: String, password: String) = ChangePasswordRequest(Some(name), password)

	// Changes password for the current user
	def changePassword(password: String) = ChangePasswordRequest(None, password)

	def disableUser(name: String) = DisableUserRequest(name)

	def enableUser(name: String) = EnableUserRequest(name)
}