package com.sksamuel.elastic4s.requests.security.users.admin

case class ChangePasswordRequest(name: Option[String], password: String)