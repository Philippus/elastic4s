package com.sksamuel.elastic4s.handlers.security.roles

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.security.roles.{
  ApplicationPrivileges,
  CreateOrUpdateRoleRequest,
  GlobalPrivileges,
  IndexPrivileges
}

object CreateOrUpdateRoleContentBuilder {
  def apply(c: CreateOrUpdateRoleRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    if (c.runAs.nonEmpty) {
      builder.autoarray("run_as", c.runAs)
    }
    if (c.clusterPermissions.nonEmpty) {
      builder.autoarray("cluster", c.clusterPermissions)
    }
    if (c.global.nonEmpty) {
      builder.rawField("global", GlobalPrivilegesContentBuilder(c.global.get))
    }
    if (c.indices.nonEmpty) {
      builder.array("indices", c.indices.map(IndexPrivilegesContentBuilder(_)).toArray)
    }
    if (c.applications.nonEmpty) {
      builder.array("applications", c.applications.map(ApplicationPrivilegesContentBuilder(_)).toArray)
    }

    builder.endObject()
    builder
  }
}

object IndexPrivilegesContentBuilder {
  def apply(i: IndexPrivileges): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.autoarray("names", i.names)
    builder.autoarray("privileges", i.privileges)

    if (
      i.field_security.nonEmpty &&
      (i.field_security.get.grant.nonEmpty || i.field_security.get.except.nonEmpty)
    ) {
      builder.startObject("field_security")
      if (i.field_security.get.grant.nonEmpty) {
        builder.autoarray("grant", i.field_security.get.grant)
      }
      if (i.field_security.get.except.nonEmpty) {
        builder.autoarray("except", i.field_security.get.except)
      }
      builder.endObject()
    }

    if (i.query.nonEmpty) {
      builder.field("query", i.query.get)
    }

    builder.endObject()
    builder
  }
}

object GlobalPrivilegesContentBuilder {
  def apply(g: GlobalPrivileges): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.startObject("application")
    builder.startObject("manage")
    builder.autoarray("applications", g.application.manage.applications)
    builder.endObject()
    builder.endObject()

    builder
  }
}

object ApplicationPrivilegesContentBuilder {
  def apply(a: ApplicationPrivileges): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.field("application", a.application)

    if (a.privileges.nonEmpty) {
      builder.autoarray("privileges", a.privileges)
    }

    if (a.resources.nonEmpty) {
      builder.autoarray("resources", a.resources)
    }

    builder
  }
}
