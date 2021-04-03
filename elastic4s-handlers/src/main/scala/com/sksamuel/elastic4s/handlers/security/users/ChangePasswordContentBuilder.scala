package com.sksamuel.elastic4s.handlers.security.users

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.security.users.admin.ChangePasswordRequest

object ChangePasswordContentBuilder {
  def apply(c: ChangePasswordRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.field("password", c.password)

    builder
  }
}
