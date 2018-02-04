package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.http.bulk.BulkHandlers
import com.sksamuel.elastic4s.http.cat.CatHandlers
import com.sksamuel.elastic4s.http.cluster.ClusterHandlers
import com.sksamuel.elastic4s.http.count.CountHandlers
import com.sksamuel.elastic4s.http.delete.DeleteHandlers
import com.sksamuel.elastic4s.http.explain.ExplainHandlers
import com.sksamuel.elastic4s.http.get.GetHandlers
import com.sksamuel.elastic4s.http.index._
import com.sksamuel.elastic4s.http.index.admin.IndexAdminHandlers
import com.sksamuel.elastic4s.http.index.alias.IndexAliasHandlers
import com.sksamuel.elastic4s.http.index.mappings.MappingHandlers
import com.sksamuel.elastic4s.http.locks.LocksHandlers
import com.sksamuel.elastic4s.http.nodes.NodesHandlers
import com.sksamuel.elastic4s.http.reindex.ReindexHandlers
import com.sksamuel.elastic4s.http.search.template.SearchTemplateHandlers
import com.sksamuel.elastic4s.http.search.{SearchHandlers, SearchScrollHandlers}
import com.sksamuel.elastic4s.http.settings.SettingsHandlers
import com.sksamuel.elastic4s.http.snapshots.SnapshotHandlers
import com.sksamuel.elastic4s.http.task.TaskHandlers
import com.sksamuel.elastic4s.http.termvectors.TermVectorHandlers
import com.sksamuel.elastic4s.http.update.UpdateHandlers
import com.sksamuel.elastic4s.http.validate.ValidateHandlers
import com.sksamuel.exts.Logging

trait ElasticDsl
  extends ElasticApi
    with Logging
    with BulkHandlers
    with CatHandlers
    with CountHandlers
    with ClusterHandlers
    with DeleteHandlers
    with ExistsHandlers
    with ExplainHandlers
    with GetHandlers
    with IndexHandlers
    with IndexAdminHandlers
    with IndexAliasHandlers
    with IndexStatsHandlers
    with IndexTemplateHandlers
    with LocksHandlers
    with MappingHandlers
    with NodesHandlers
    with ReindexHandlers
    with RolloverHandlers
    with SearchHandlers
    with SearchTemplateHandlers
    with SearchScrollHandlers
    with SettingsHandlers
    with SnapshotHandlers
    with UpdateHandlers
    with TaskHandlers
    with TermVectorHandlers
    with ValidateHandlers {

  implicit class RichRequest[T](t: T) {
    def show(implicit handler: Handler[T, _]): String = ElasticRequestShow.show(handler.requestHandler(t))
  }
}

object ElasticDsl extends ElasticDsl
