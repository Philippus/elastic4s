package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.security.users.DeleteUserRequest

trait DeleteUserApi {
  def deleteUser(name: String): DeleteUserRequest = DeleteUserRequest(name)
}
