package com.sksamuel.elastic4s.requests.security.users

trait CreateUserApi {
	def createUser(
		name: String,
		enabled: Option[Boolean]=None,
		email: Option[String]=None,
		fullName: Option[String]=None,
		metadata: Map[String,Any]=Map(),
		password: UserPassword,
		roles: Seq[String]=Seq()
	) = CreateOrUpdateUserRequest(name, CreateUser, enabled, email, fullName, metadata, Some(password), roles)

	def updateUser(
		name: String,
		enabled: Option[Boolean]=None,
		email: Option[String]=None,
		fullName: Option[String]=None,
		metadata: Map[String,Any]=Map(),
		password: Option[UserPassword]=None,
		roles: Seq[String]=Seq()
	) = CreateOrUpdateUserRequest(name, UpdateUser, enabled, email, fullName, metadata, password, roles)
}