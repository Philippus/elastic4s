package com.sksamuel.elastic4s.handlers.security.roles

import com.sksamuel.elastic4s.requests.security.roles.{GetRoleRequest, GetRoleResponse}
import com.sksamuel.elastic4s.{ElasticRequest, Handler}

import java.net.URLEncoder

trait RoleHandlers {
  private val ROLE_BASE_PATH = "/_security/role/"

  implicit object GetRoleHandler extends Handler[GetRoleRequest, Map[String, GetRoleResponse]] {

    override def build(request: GetRoleRequest): ElasticRequest = {
      val endpoint = ROLE_BASE_PATH + URLEncoder.encode(request.name, "UTF-8")
      ElasticRequest("GET", endpoint)
    }
  }
}
