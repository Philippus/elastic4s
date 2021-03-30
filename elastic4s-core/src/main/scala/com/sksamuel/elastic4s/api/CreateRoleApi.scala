package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.security.roles.{ApplicationPrivileges, CreateOrUpdateRoleRequest, CreateRole, GlobalPrivileges, IndexPrivileges, UpdateRole}

trait CreateRoleApi {
  def createRole(
                  name: String,
                  runAs: Seq[String] = Seq(),
                  clusterPermissions: Seq[String] = Seq(),
                  global: Option[GlobalPrivileges] = None,
                  indices: Seq[IndexPrivileges] = Seq(),
                  applications: Seq[ApplicationPrivileges] = Seq()
                ) = CreateOrUpdateRoleRequest(name, CreateRole, runAs, clusterPermissions, global, indices, applications)

  def updateRole(
                  name: String,
                  runAs: Seq[String] = Seq(),
                  clusterPermissions: Seq[String] = Seq(),
                  global: Option[GlobalPrivileges] = None,
                  indices: Seq[IndexPrivileges] = Seq(),
                  applications: Seq[ApplicationPrivileges] = Seq()
                ) = CreateOrUpdateRoleRequest(name, UpdateRole, runAs, clusterPermissions, global, indices, applications)
}
