package com.sksamuel.elastic4s.requests.security.roles

sealed trait RoleAction
case object CreateRole extends RoleAction
case object UpdateRole extends RoleAction

case class CreateOrUpdateRoleRequest(
	name: String,
	action: RoleAction,
	runAs: Seq[String]=Seq(),
	clusterPermissions: Seq[String]=Seq(),
	global: Option[GlobalPrivileges]=None,
	indices: Seq[IndexPrivileges]=Seq(),
	applications: Seq[ApplicationPrivileges]=Seq()
)