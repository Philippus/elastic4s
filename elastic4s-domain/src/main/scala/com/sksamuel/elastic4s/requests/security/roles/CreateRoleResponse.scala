package com.sksamuel.elastic4s.requests.security.roles

case class CreateRoleResponse(role: RoleCreated)

case class RoleCreated(created: Boolean)