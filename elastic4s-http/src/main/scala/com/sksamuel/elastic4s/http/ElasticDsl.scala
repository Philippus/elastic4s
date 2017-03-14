package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.http.alias.AliasImplicits
import com.sksamuel.elastic4s.http.bulk.BulkImplicits
import com.sksamuel.elastic4s.http.cluster.ClusterImplicits
import com.sksamuel.elastic4s.http.delete.DeleteImplicits
import com.sksamuel.elastic4s.http.explain.ExplainImplicits
import com.sksamuel.elastic4s.http.get.GetImplicits
import com.sksamuel.elastic4s.http.index.{IndexAdminImplicits, IndexImplicits, IndexTemplateImplicits}
import com.sksamuel.elastic4s.http.nodes.NodesImplicits
import com.sksamuel.elastic4s.http.search.{SearchImplicits, SearchScrollImplicits}
import com.sksamuel.elastic4s.http.task.TaskImplicits
import com.sksamuel.elastic4s.http.update.UpdateImplicits
import com.sksamuel.elastic4s.http.validate.ValidateImplicits
import com.sksamuel.exts.Logging

trait ElasticDsl
  extends ElasticApi
    with Logging
    with AliasImplicits
    with BulkImplicits
    with ClusterImplicits
    with DeleteImplicits
    with ExplainImplicits
    with GetImplicits
    with IndexImplicits
    with IndexAdminImplicits
    with IndexTemplateImplicits
    with LocksImplicits
    with NodesImplicits
    with SearchImplicits
    with SearchScrollImplicits
    with UpdateImplicits
    with TaskImplicits
    with ValidateImplicits

object ElasticDsl extends ElasticDsl
