package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.http.bulk.BulkExecutables
import com.sksamuel.elastic4s.http.delete.DeleteExecutables
import com.sksamuel.elastic4s.http.get.GetHttpExecutables
import com.sksamuel.elastic4s.http.index.{IndexAdminExecutables, IndexHttpExecutables, RefreshIndexExecutables}
import com.sksamuel.elastic4s.http.update.UpdateExecutables
import com.sksamuel.exts.Logging

trait ElasticDsl
  extends ElasticApi
    with Logging
    with BulkExecutables
    with DeleteExecutables
    with GetHttpExecutables
    with IndexAdminExecutables
    with IndexHttpExecutables
    with RefreshIndexExecutables
    with UpdateExecutables

object ElasticDsl extends ElasticDsl
