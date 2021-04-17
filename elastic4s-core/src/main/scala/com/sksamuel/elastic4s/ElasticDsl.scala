package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.handlers.alias.IndexAliasHandlers
import com.sksamuel.elastic4s.handlers.bulk.BulkHandlers
import com.sksamuel.elastic4s.handlers.cat.CatHandlers
import com.sksamuel.elastic4s.handlers.cluster.ClusterHandlers
import com.sksamuel.elastic4s.handlers.count.CountHandlers
import com.sksamuel.elastic4s.handlers.delete.DeleteHandlers
import com.sksamuel.elastic4s.handlers.exists.ExistsHandlers
import com.sksamuel.elastic4s.handlers.explain.ExplainHandlers
import com.sksamuel.elastic4s.handlers.get.GetHandlers
import com.sksamuel.elastic4s.handlers.index.mapping.MappingHandlers
import com.sksamuel.elastic4s.handlers.index.{IndexAdminHandlers, IndexHandlers, IndexStatsHandlers, IndexTemplateHandlers, RolloverHandlers}
import com.sksamuel.elastic4s.handlers.locks.LocksHandlers
import com.sksamuel.elastic4s.handlers.nodes.NodesHandlers
import com.sksamuel.elastic4s.handlers.reindex.ReindexHandlers
import com.sksamuel.elastic4s.handlers.security.roles.{RoleAdminHandlers, RoleHandlers}
import com.sksamuel.elastic4s.handlers.security.users.{UserAdminHandlers, UserHandlers}
import com.sksamuel.elastic4s.handlers.settings.SettingsHandlers
import com.sksamuel.elastic4s.handlers.snapshot.SnapshotHandlers
import com.sksamuel.elastic4s.handlers.task.TaskHandlers
import com.sksamuel.elastic4s.handlers.termvectors.TermVectorHandlers
import com.sksamuel.elastic4s.handlers.update.UpdateHandlers
import com.sksamuel.elastic4s.handlers.validate.ValidateHandlers
import com.sksamuel.elastic4s.requests.searches.template.SearchTemplateHandlers
import com.sksamuel.elastic4s.requests.searches.{AsyncSearchHandlers, SearchHandlers, SearchScrollHandlers}
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
    with RoleAdminHandlers
    with RoleHandlers
    with RolloverHandlers
    with SearchHandlers
    with SearchTemplateHandlers
    with SearchScrollHandlers
    with SettingsHandlers
    with SnapshotHandlers
    with UpdateHandlers
    with TaskHandlers
    with TermVectorHandlers
    with UserAdminHandlers
    with UserHandlers
    with ValidateHandlers
    with AsyncSearchHandlers {

  implicit class RichRequest[T](t: T) {
    def request(implicit handler: Handler[T, _]): ElasticRequest = handler.build(t)
    def show(implicit handler: Handler[T, _]): String            = Show[ElasticRequest].show(handler.build(t))
  }
}

object ElasticDsl extends ElasticDsl
