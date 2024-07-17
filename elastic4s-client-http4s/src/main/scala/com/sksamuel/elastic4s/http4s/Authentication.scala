package com.sksamuel.elastic4s.http4s

sealed trait Authentication

object Authentication {
  case class UsernamePassword(username: String, password: String) extends Authentication

  case class ApiKey(apiKey: String) extends Authentication

  case object NoAuth extends Authentication
}
