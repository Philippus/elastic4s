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
import com.sksamuel.elastic4s.handlers.pit.PitHandlers
import com.sksamuel.elastic4s.handlers.reindex.ReindexHandlers
import com.sksamuel.elastic4s.handlers.reloadsearchanalyzers.ReloadSearchAnalyzersHandlers
import com.sksamuel.elastic4s.handlers.script.StoredScriptHandlers
import com.sksamuel.elastic4s.handlers.security.roles.{RoleAdminHandlers, RoleHandlers}
import com.sksamuel.elastic4s.handlers.security.users.{UserAdminHandlers, UserHandlers}
import com.sksamuel.elastic4s.handlers.settings.SettingsHandlers
import com.sksamuel.elastic4s.handlers.snapshot.SnapshotHandlers
import com.sksamuel.elastic4s.handlers.synonyms.SynonymsHandlers
import com.sksamuel.elastic4s.handlers.task.TaskHandlers
import com.sksamuel.elastic4s.handlers.termsenum.TermsEnumHandlers
import com.sksamuel.elastic4s.handlers.termvectors.TermVectorHandlers
import com.sksamuel.elastic4s.handlers.update.UpdateHandlers
import com.sksamuel.elastic4s.handlers.validate.ValidateHandlers
import com.sksamuel.elastic4s.json.XContentBuilder
import com.sksamuel.elastic4s.requests.ingest.IngestHandlers
import com.sksamuel.elastic4s.requests.searches.aggs.AbstractAggregation
import com.sksamuel.elastic4s.requests.searches.template.SearchTemplateHandlers
import com.sksamuel.elastic4s.requests.searches.{SearchHandlers, SearchScrollHandlers, defaultCustomAggregationHandler}

trait ElasticDslWithoutSearch extends

ElasticApi
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
with IngestHandlers
with LocksHandlers
with MappingHandlers
with NodesHandlers
with ReindexHandlers
with ReloadSearchAnalyzersHandlers
with RoleAdminHandlers
with RoleHandlers
with RolloverHandlers
with SearchTemplateHandlers
with SearchScrollHandlers
with SettingsHandlers
with SnapshotHandlers
with StoredScriptHandlers
with SynonymsHandlers
with UpdateHandlers
with TaskHandlers
with TermVectorHandlers
with TermsEnumHandlers
with UserAdminHandlers
with UserHandlers
with ValidateHandlers
with PitHandlers {

implicit class RichRequest[T](t: T) {
  def request(implicit handler: Handler[T, _]): ElasticRequest = handler.build(t)

  def show(implicit handler: Handler[T, _]): String = Show[ElasticRequest].show(handler.build(t))
}
}

trait ElasticDsl
    extends ElasticDslWithoutSearch
    with SearchHandlers

object ElasticDsl extends ElasticDsl {
  def withCustomAggregationHandler(customAggregationHandler: PartialFunction[AbstractAggregation, XContentBuilder])= new ElasticDslWithoutSearch {
    implicit val customBaseSearchHandler: BaseSearchHandler = new BaseSearchHandler(customAggregationHandler.orElse(defaultCustomAggregationHandler))

    implicit val customBaseMultiSearchHandler: BaseMultiSearchHandler = new BaseMultiSearchHandler(customAggregationHandler.orElse(defaultCustomAggregationHandler))
  }
}
