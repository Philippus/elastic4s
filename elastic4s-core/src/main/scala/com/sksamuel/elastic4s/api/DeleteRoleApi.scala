package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.security.roles.DeleteRoleRequest

trait DeleteRoleApi {
  def deleteRole(name: String): DeleteRoleRequest = DeleteRoleRequest(name)
}
