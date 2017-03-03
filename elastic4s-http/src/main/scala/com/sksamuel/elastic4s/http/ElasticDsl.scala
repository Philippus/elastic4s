package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.http.bulk.BulkImplicits
import com.sksamuel.elastic4s.http.delete.DeleteImplicits
import com.sksamuel.elastic4s.http.get.GetImplicits
import com.sksamuel.elastic4s.http.index.{IndexAdminImplicits, IndexImplicits}
import com.sksamuel.elastic4s.http.search.{SearchImplicits, SearchScrollImplicits}
import com.sksamuel.elastic4s.http.task.TaskImplicits
import com.sksamuel.elastic4s.http.update.UpdateImplicits
import com.sksamuel.elastic4s.http.validate.ValidateImplicits
import com.sksamuel.exts.Logging

trait ElasticDsl
  extends ElasticApi
    with Logging
    with BulkImplicits
    with DeleteImplicits
    with GetImplicits
    with IndexAdminImplicits
    with IndexImplicits
    with LocksImplicits
    with SearchImplicits
    with SearchScrollImplicits
    with UpdateImplicits
    with TaskImplicits
    with ValidateImplicits

object ElasticDsl extends ElasticDsl
