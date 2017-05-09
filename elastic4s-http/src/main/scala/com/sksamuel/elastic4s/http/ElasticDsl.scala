package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.http.index.alias.IndexAliasImplicits
import com.sksamuel.elastic4s.http.bulk.BulkImplicits
import com.sksamuel.elastic4s.http.cat.CatImplicits
import com.sksamuel.elastic4s.http.cluster.ClusterImplicits
import com.sksamuel.elastic4s.http.delete.DeleteImplicits
import com.sksamuel.elastic4s.http.explain.ExplainImplicits
import com.sksamuel.elastic4s.http.get.GetImplicits
import com.sksamuel.elastic4s.http.index.admin.IndexAdminImplicits
import com.sksamuel.elastic4s.http.index.mappings.MappingExecutables
import com.sksamuel.elastic4s.http.index.{IndexImplicits, IndexTemplateImplicits}
import com.sksamuel.elastic4s.http.nodes.NodesImplicits
import com.sksamuel.elastic4s.http.search.template.SearchTemplateImplicits
import com.sksamuel.elastic4s.http.search.{SearchImplicits, SearchScrollImplicits}
import com.sksamuel.elastic4s.http.task.TaskImplicits
import com.sksamuel.elastic4s.http.termvectors.TermVectorsExecutables
import com.sksamuel.elastic4s.http.update.UpdateImplicits
import com.sksamuel.elastic4s.http.validate.ValidateImplicits
import com.sksamuel.exts.Logging

trait ElasticDsl
  extends ElasticApi
    with Logging
    with BulkImplicits
    with CatImplicits
    with ClusterImplicits
    with DeleteImplicits
    with ExplainImplicits
    with GetImplicits
    with IndexImplicits
    with IndexAdminImplicits
    with IndexAliasImplicits
    with IndexTemplateImplicits
    with LocksImplicits
    with MappingExecutables
    with NodesImplicits
    with SearchImplicits
    with SearchTemplateImplicits
    with SearchScrollImplicits
    with UpdateImplicits
    with TaskImplicits
    with TermVectorsExecutables
    with ValidateImplicits

object ElasticDsl extends ElasticDsl
