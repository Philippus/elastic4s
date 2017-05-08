package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.admin.IndexAdminApi
import com.sksamuel.elastic4s.alias.AliasesApi
import com.sksamuel.elastic4s.analyzers.{AnalyzerApi, NormalizerApi, TokenizerApi}
import com.sksamuel.elastic4s.bulk.BulkApi
import com.sksamuel.elastic4s.cat.CatsApi
import com.sksamuel.elastic4s.cluster.ClusterApi
import com.sksamuel.elastic4s.delete.DeleteApi
import com.sksamuel.elastic4s.explain.ExplainApi
import com.sksamuel.elastic4s.get.GetApi
import com.sksamuel.elastic4s.indexes.admin.{ForceMergeApi, IndexRecoveryApi}
import com.sksamuel.elastic4s.indexes.{CreateIndexApi, DeleteIndexApi, IndexApi, IndexTemplateApi}
import com.sksamuel.elastic4s.locks.LocksApi
import com.sksamuel.elastic4s.mappings.dynamictemplate.DynamicTemplateApi
import com.sksamuel.elastic4s.mappings.{MappingApi, TimestampDefinition}
import com.sksamuel.elastic4s.nodes.NodesApi
import com.sksamuel.elastic4s.reindex.ReindexApi
import com.sksamuel.elastic4s.script.ScriptApi
import com.sksamuel.elastic4s.searches.aggs.AggregationApi
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationApi
import com.sksamuel.elastic4s.searches.collapse.CollapseApi
import com.sksamuel.elastic4s.searches.queries.funcscorer.ScoreApi
import com.sksamuel.elastic4s.searches.sort.SortApi
import com.sksamuel.elastic4s.searches.suggestion.SuggestionApi
import com.sksamuel.elastic4s.searches._
import com.sksamuel.elastic4s.task.TaskApi
import com.sksamuel.elastic4s.termvectors.TermVectorApi
import com.sksamuel.elastic4s.update.UpdateApi
import com.sksamuel.elastic4s.validate.ValidateApi

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

// contains all the syntactic definitions
trait ElasticApi
  extends ElasticImplicits
    with AliasesApi
    with AggregationApi
    with AnalyzerApi
    with BulkApi
    with CatsApi
    with CreateIndexApi
    with ClusterApi
    with CollapseApi
    with DeleteApi
    with DeleteIndexApi
    with DynamicTemplateApi
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
    with SortApi
    with SuggestionApi
    with TaskApi
    with TermVectorApi
    with TokenizerApi
    with TypesApi
    with UpdateApi
    with ValidateApi {

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 10.seconds): T = Await.result(future, duration)
  }

  def timestamp(en: Boolean): TimestampDefinition = TimestampDefinition(en)
}

object ElasticApi extends ElasticApi
