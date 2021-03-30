package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.security.users.GetUserRequest

trait UserApi {
  // Returns the named user
  def getUser(name: String) = GetUserRequest(name)

  // Returns all roles
  def getUsers() = GetUserRequest("")
}
