package com.sksamuel.elastic4s.requests.security.roles

case class IndexPrivileges(
	names: Seq[String],
	privileges: Seq[String],
	field_security: Option[FieldSecurity]=None,
	query: Option[String]=None,
	allow_restricted_indices: Option[Boolean]=None
)

case class FieldSecurity(grant: Seq[String]=Seq(), except: Seq[String]=Seq())