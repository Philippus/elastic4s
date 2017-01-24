package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.admin.IndexAdminApi
import com.sksamuel.elastic4s.alias.AliasesApi
import com.sksamuel.elastic4s.analyzers.{AnalyzerApi, TokenizerApi}
import com.sksamuel.elastic4s.bulk.BulkApi
import com.sksamuel.elastic4s.delete.DeleteApi
import com.sksamuel.elastic4s.explain.ExplainApi
import com.sksamuel.elastic4s.get.GetApi
import com.sksamuel.elastic4s.indexes.{CreateIndexApi, IndexApi}
import com.sksamuel.elastic4s.mappings.{DynamicTemplateApi, MappingApi, TimestampDefinition}
import com.sksamuel.elastic4s.reindex.ReindexApi
import com.sksamuel.elastic4s.script.ScriptApi
import com.sksamuel.elastic4s.searches.HighlightApi
import com.sksamuel.elastic4s.task.TaskApi
import com.sksamuel.elastic4s.termvectors.TermVectorApi
import com.sksamuel.elastic4s.update.UpdateApi
import com.sksamuel.elastic4s.validate.ValidateApi

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

// contains all the syntactic definitions
trait ElasticApi
  extends AliasesApi
    with AnalyzerApi
    with BulkApi
    with CreateIndexApi
    with DeleteApi
    with DynamicTemplateApi
    with ExplainApi
    with GetApi
    with HighlightApi
    with IndexApi
    with IndexAdminApi
    with MappingApi
    with ReindexApi
    with ScriptApi
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
