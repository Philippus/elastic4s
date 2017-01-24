package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.http.delete.DeleteExecutables
import com.sksamuel.elastic4s.http.get.GetHttpExecutables
import com.sksamuel.elastic4s.http.index.{IndexAdminExecutables, IndexHttpExecutables, RefreshIndexExecutables}
import com.sksamuel.exts.Logging

trait ElasticDsl
  extends ElasticApi
    with Logging
    with IndexAdminExecutables
    with DeleteExecutables
    with GetHttpExecutables
    with IndexHttpExecutables
    with RefreshIndexExecutables

object ElasticDsl extends ElasticDsl
