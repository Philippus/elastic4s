package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object CreateOrUpdateRoleContentBuilder {
	def apply(c: CreateOrUpdateRoleRequest): XContentBuilder = {
		val builder = XContentFactory.jsonBuilder()

		if (c.runAs.length > 0) {
			builder.autoarray("run_as", c.runAs)
		}
		if (c.clusterPermissions.length > 0) {
			builder.autoarray("cluster", c.clusterPermissions)
		}
		if (c.global.nonEmpty) {
			builder.rawField("global", GlobalPrivilegesContentBuilder(c.global.get))
		}
		if (c.indices.length > 0) {
			builder.array("indices", c.indices.map(IndexPrivilegesContentBuilder(_)).toArray)
		}
		if (c.applications.length > 0) {
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
			(i.field_security.get.grant.length > 0 || i.field_security.get.except.length > 0)
		) {
			builder.startObject("field_security")
			if (i.field_security.get.grant.length > 0) {
				builder.autoarray("grant", i.field_security.get.grant)
			}
			if (i.field_security.get.except.length > 0) {
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

		if (a.privileges.length > 0) {
			builder.autoarray("privileges", a.privileges)
		}

		if (a.resources.length > 0) {
			builder.autoarray("resources", a.resources)
		}		

		builder
	}
}