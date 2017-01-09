package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.http.get.GetHttpExecutable

trait HttpDsl {
  implicit val _get = GetHttpExecutable
}
