package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.security.roles.admin.ClearRolesCacheRequest

trait ClearRolesCacheApi {
  def clearRolesCache(name: String) = ClearRolesCacheRequest(name)
}
