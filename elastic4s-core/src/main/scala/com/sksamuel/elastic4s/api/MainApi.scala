package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.main.MainRequest

trait MainApi {
  def serverInfo: MainRequest = MainRequest()
}
