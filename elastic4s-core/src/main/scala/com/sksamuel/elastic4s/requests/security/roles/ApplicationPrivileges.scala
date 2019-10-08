package com.sksamuel.elastic4s.requests.security.roles

case class ApplicationPrivileges(
	application: String,
	privileges: Seq[String]=Seq(),
	resources: Seq[String]=Seq()
)