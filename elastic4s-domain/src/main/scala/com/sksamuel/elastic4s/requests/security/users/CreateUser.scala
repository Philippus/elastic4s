package com.sksamuel.elastic4s.requests.security.users

sealed trait UserAction
case object CreateUser extends UserAction
case object UpdateUser extends UserAction

sealed trait UserPassword
case class PlaintextPassword(value: String) extends UserPassword
case class PasswordHash(value: String) extends UserPassword

case class CreateOrUpdateUserRequest(
	name: String,
	action: UserAction,
	enabled: Option[Boolean]=None,
	email: Option[String]=None,
	fullName: Option[String]=None,
	metadata: Map[String,Any]=Map(),
	password: Option[UserPassword],
	roles: Seq[String]=Seq()
)