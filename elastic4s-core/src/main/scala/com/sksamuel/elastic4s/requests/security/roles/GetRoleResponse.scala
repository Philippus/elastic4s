package com.sksamuel.elastic4s.requests.security.roles

case class GetRoleResponse(
	cluster: Seq[String]=Seq(),
	indices: Seq[IndexPrivileges]=Seq(),
	global: Seq[GlobalPrivileges]=Seq(),
	applications: Seq[ApplicationPrivileges]=Seq(),
	run_as: Seq[String]=Seq(),
	metadata: Option[RoleMetadata]=None,
	transient_metadata: Option[RoleTransientMetadata]=None
)