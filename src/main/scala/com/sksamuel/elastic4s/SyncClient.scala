package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.mappings.GetMappingDefinition
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse
import org.elasticsearch.action.explain.ExplainResponse
import org.elasticsearch.action.get.MultiGetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.percolate.PercolateResponse
import org.elasticsearch.action.search.{ MultiSearchResponse, SearchResponse }

import scala.concurrent.{ ExecutionContext, Await }
import scala.concurrent.duration.Duration

@deprecated("Use .await() on future of async client", "1.3.0")
class SyncClient(client: ElasticClient)(implicit duration: Duration) extends ElasticDsl {

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(i: IndexDefinition)(implicit duration: Duration) = Await.result(client.execute(i), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(c: CountDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(d: DeleteIndexDefinition)(implicit duration: Duration) = Await.result(client.execute(d), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(c: CreateIndexDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(c: MoreLikeThisDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(c: UpdateDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(c: ValidateDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(o: OptimizeDefinition)(implicit duration: Duration) = Await.result(client.execute(o), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(get: GetDefinition)(implicit duration: Duration) = Await.result(client.execute(get), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(percolateDef: PercolateDefinition)(implicit duration: Duration): PercolateResponse =
    Await.result(client.execute(percolateDef), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(registerDef: RegisterDefinition)(implicit duration: Duration): IndexResponse =
    Await.result(client.execute(registerDef), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(ddef: DeleteByIdDefinition)(implicit duration: Duration): DeleteResponse =
    Await.result(client.execute(ddef), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(ddef: DeleteByQueryDefinition)(implicit duration: Duration): DeleteByQueryResponse =
    Await.result(client.execute(ddef), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(gets: MultiGetDefinition)(implicit duration: Duration): MultiGetResponse =
    Await.result(client.execute(gets), duration)

  @deprecated("use execute", "1.3.3")
  def search(searches: SearchDefinition)(implicit duration: Duration): SearchResponse = execute(searches)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(search: SearchDefinition)(implicit duration: Duration): SearchResponse =
    Await.result(client.execute(search), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(searches: SearchDefinition*)(implicit duration: Duration): MultiSearchResponse =
    Await.result(client.execute(new MultiSearchDefinition(searches)), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(definition: ExplainDefinition)(implicit duration: Duration): ExplainResponse =
    Await.result(client.execute(definition), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def exists(indexes: String*): IndicesExistsResponse = Await.result(client.exists(indexes: _*), duration)

  @deprecated("Use .await() on future of async client", "1.4.4")
  def reindex(sourceIndex: String, targetIndex: String, chunkSize: Int = 500, scroll: String = "5m")(implicit ec: ExecutionContext, duration: Duration): Unit = {
    Await.result(client.reindex(sourceIndex, targetIndex, chunkSize, scroll), duration)
  }

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(get: GetMappingDefinition)(implicit duration: Duration): GetMappingsResponse = {
    Await.result(client.execute(get), duration)
  }

  @deprecated("Use .await() on future of async client", "1.3.0")
  def execute(put: PutMappingDefinition)(implicit duration: Duration): PutMappingResponse = {
    Await.result(client.execute(put), duration)
  }

  @deprecated("Use .await() on future of async client", "1.4.4")
  def execute(bulk: BulkDefinition)(implicit duration: Duration): BulkResponse = {
    Await.result(client.execute(bulk), duration)
  }
}
