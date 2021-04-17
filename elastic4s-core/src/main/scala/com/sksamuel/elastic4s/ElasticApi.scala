package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.api.{AggregationApi, AliasesApi, AnalyzeApi, AnalyzerApi, AsyncSearchApi, BulkApi, CatsApi, ClearRolesCacheApi, ClusterApi, CollapseApi, CountApi, CreateIndexApi, CreateRoleApi, CreateUserApi, DeleteApi, DeleteIndexApi, DeleteRoleApi, DeleteUserApi, ExistsApi, ExplainApi, ForceMergeApi, GetApi, HighlightApi, IndexAdminApi, IndexApi, IndexRecoveryApi, IndexTemplateApi, IngestApi, LocksApi, MappingApi, NodesApi, NormalizerApi, PipelineAggregationApi, QueryApi, ReindexApi, RoleApi, ScoreApi, ScriptApi, ScrollApi, SearchApi, SearchTemplateApi, SettingsApi, SnapshotApi, SortApi, SuggestionApi, TaskApi, TermVectorApi, TokenFilterApi, TokenizerApi, TypesApi, UpdateApi, UserAdminApi, UserApi, ValidateApi}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

// contains all the syntactic definitions
trait ElasticApi
  extends AliasesApi
    with AggregationApi
    with AnalyzerApi
    with BulkApi
    with CatsApi
    with CreateIndexApi
    with ClearRolesCacheApi
    with ClusterApi
    with CollapseApi
    with CountApi
    with CreateRoleApi
    with CreateUserApi
    with DeleteApi
    with DeleteIndexApi
    with DeleteRoleApi
    with DeleteUserApi
    with ExistsApi
    with ExplainApi
    with ForceMergeApi
    with GetApi
    with HighlightApi
    with IndexApi
    with IndexAdminApi
    with AnalyzeApi
    with IndexRecoveryApi
    with IndexTemplateApi
    with IngestApi
    with LocksApi
    with MappingApi
    with NodesApi
    with NormalizerApi
    with QueryApi
    with PipelineAggregationApi
    with ReindexApi
    with RoleApi
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
    with UserAdminApi
    with UserApi
    with ValidateApi
    with AsyncSearchApi {

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 60.seconds): T = Await.result(future, duration)
  }
}

object ElasticApi extends ElasticApi
