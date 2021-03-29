package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.security.roles.GetRoleRequest

trait RoleApi {
  // Returns the named role
  def getRole(name: String) = GetRoleRequest(name)

  // Returns all roles
  def getRoles() = GetRoleRequest("")
}
