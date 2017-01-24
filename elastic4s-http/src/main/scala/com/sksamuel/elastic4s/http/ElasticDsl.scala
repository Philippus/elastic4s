package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.http.delete.DeleteExecutables
import com.sksamuel.elastic4s.http.get.GetHttpExecutables
import com.sksamuel.elastic4s.http.index.{CreateIndexExecutables, IndexHttpExecutables, RefreshIndexExecutables}
import com.sksamuel.exts.Logging

trait ElasticDsl
  extends Logging
    with CreateIndexExecutables
    with DeleteExecutables
    with GetHttpExecutables
    with IndexHttpExecutables
    with RefreshIndexExecutables

object ElasticDsl extends ElasticDsl
