package com.sksamuel.elastic4s.requests.security.users

case class GetUserResponse(
	username: String,
	roles: Seq[String],
	full_name: String,
	email: String,
	metadata: Map[String, Any],
	enabled: Boolean
)