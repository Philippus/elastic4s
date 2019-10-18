package com.sksamuel.elastic4s.requests.security.users.admin

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ChangePasswordContentBuilder {
	def apply(c: ChangePasswordRequest): XContentBuilder = {
		val builder = XContentFactory.jsonBuilder()

		builder.field("password", c.password)

		builder
	}
}