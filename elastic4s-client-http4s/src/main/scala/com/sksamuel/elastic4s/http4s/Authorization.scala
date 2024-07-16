package com.sksamuel.elastic4s.http4s

sealed trait Authorization

object Authorization {
  case class UsernamePassword(username: String, password: String) extends Authorization
  case object NoAuth extends Authorization
}
