package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object CreateOrUpdateUserContentBuilder {
	def apply(c: CreateOrUpdateUserRequest): XContentBuilder = {
		val builder = XContentFactory.jsonBuilder()

		if (c.enabled.nonEmpty) {
			builder.field("enabled", c.enabled.get)
		}

		if (c.email.nonEmpty) {
			builder.field("email", c.email.get)
		}

		if (c.fullName.nonEmpty) {
			builder.field("full_name", c.fullName.get)
		}

		if (c.metadata.size > 0) {
			builder.startObject("metadata")

			c.metadata.foreach { case (key, value) =>
				builder.autofield(key, value)
			}
		
			builder.endObject()
		}

		c.password match {
			case Some(pass) => pass match {
				case p: PlaintextPassword => builder.field("password", p.value)
				case h: PasswordHash => builder.field("password_hash", h.value)
			}
			case None => c.action match {
				case CreateUser => throw new IllegalArgumentException("CreateUser request requires either a password or a password hash")
				case UpdateUser => {}
			}
		}

		builder.array("roles", c.roles.toArray)

		builder.endObject()
		builder
	}
}