package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.http.get.GetHttpExecutables
import com.sksamuel.elastic4s.http.index.IndexHttpExecutables
import com.sksamuel.exts.Logging

trait HttpDsl
  extends Logging
    with IndexHttpExecutables
    with GetHttpExecutables

object HttpDsl extends HttpDsl
