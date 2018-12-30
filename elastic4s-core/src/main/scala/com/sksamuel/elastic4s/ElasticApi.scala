package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.requests.{ExistsApi, TypesApi}
import com.sksamuel.elastic4s.requests.cluster.ClusterApi
import com.sksamuel.elastic4s.requests.count.CountApi
import com.sksamuel.elastic4s.requests.delete.DeleteApi
import com.sksamuel.elastic4s.requests.explain.ExplainApi
import com.sksamuel.elastic4s.requests.indexes.admin.{ForceMergeApi, IndexRecoveryApi}
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexApi, DeleteIndexApi, IndexApi, IndexTemplateApi}
import com.sksamuel.elastic4s.requests.mappings.MappingApi
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateApi
import com.sksamuel.elastic4s.requests.admin.IndexAdminApi
import com.sksamuel.elastic4s.requests.alias.AliasesApi
import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerApi, NormalizerApi, TokenFilterApi, TokenizerApi}
import com.sksamuel.elastic4s.requests.nodes.NodesApi
import com.sksamuel.elastic4s.requests.reindex.ReindexApi
import com.sksamuel.elastic4s.requests.bulk.BulkApi
import com.sksamuel.elastic4s.requests.cat.CatsApi
import com.sksamuel.elastic4s.requests.get.GetApi
import com.sksamuel.elastic4s.requests.locks.LocksApi
import com.sksamuel.elastic4s.requests.script.ScriptApi
import com.sksamuel.elastic4s.requests.searches._
import com.sksamuel.elastic4s.requests.searches.aggs.AggregationApi
import com.sksamuel.elastic4s.requests.searches.aggs.pipeline.PipelineAggregationApi
import com.sksamuel.elastic4s.requests.searches.collapse.CollapseApi
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.ScoreApi
import com.sksamuel.elastic4s.requests.searches.sort.SortApi
import com.sksamuel.elastic4s.requests.searches.suggestion.SuggestionApi
import com.sksamuel.elastic4s.requests.settings.SettingsApi
import com.sksamuel.elastic4s.requests.snapshots.SnapshotApi
import com.sksamuel.elastic4s.requests.task.TaskApi
import com.sksamuel.elastic4s.requests.termvectors.TermVectorApi
import com.sksamuel.elastic4s.requests.update.UpdateApi
import com.sksamuel.elastic4s.requests.validate.ValidateApi

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

// contains all the syntactic definitions
trait ElasticApi
  extends AliasesApi
    with ElasticImplicits
    with AggregationApi
    with AnalyzerApi
    with BulkApi
    with CatsApi
    with CreateIndexApi
    with ClusterApi
    with CollapseApi
    with CountApi
    with DeleteApi
    with DeleteIndexApi
    with DynamicTemplateApi
    with ExistsApi
    with ExplainApi
    with ForceMergeApi
    with GetApi
    with HighlightApi
    with IndexApi
    with IndexAdminApi
    with IndexRecoveryApi
    with IndexTemplateApi
    with LocksApi
    with MappingApi
    with NodesApi
    with NormalizerApi
    with QueryApi
    with PipelineAggregationApi
    with ReindexApi
    with ScriptApi
    with ScoreApi
    with ScrollApi
    with SearchApi
    with SearchTemplateApi
    with SettingsApi
    with SnapshotApi
    with SortApi
    with SuggestionApi
    with TaskApi
    with TermVectorApi
    with TokenizerApi
    with TokenFilterApi
    with TypesApi
    with UpdateApi
    with ValidateApi {

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 60.seconds): T = Await.result(future, duration)
  }
}

object ElasticApi extends ElasticApi
