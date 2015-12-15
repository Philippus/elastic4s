package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.source.JsonDocumentSource
import org.elasticsearch.client.Client

import scala.concurrent.{ExecutionContext, Future}

case class ReindexDefinition(sourceIndex: String,
                             targetIndex: String,
                             chunkSize: Int = 500,
                             keepAlive: String = "5m",
                             preserveId: Boolean = true,
                             selector:QueryDefinition = ElasticDsl.matchAllQuery)(implicit val executor: ExecutionContext)

trait ReindexDsl {

  implicit object ReindexExecutable extends Executable[ReindexDefinition, Unit, Unit] {
    override def apply(client: Client, d: ReindexDefinition): Future[Unit] = {

      import ElasticDsl._
      import d.executor

      val query = {
        search in d.sourceIndex limit d.chunkSize scroll d.keepAlive searchType SearchType.Scan query d.selector
      }

      SearchDefinitionExecutable.apply(client, query) flatMap { response =>
        def _scroll(scrollId: String): Future[Unit] = {
          ScrollExecutable.apply(
            client,
            search scroll scrollId keepAlive d.keepAlive
          ) flatMap { response =>
            if (response.nonEmpty) {
              val indexReqs = response.hits.map(hit => (hit.`type`, hit.id, hit.sourceAsString)).collect {
                case (typ, _id, source) =>
                  val expr = index into d.targetIndex -> typ
                  (if (d.preserveId) expr id _id else expr) doc JsonDocumentSource(source)
              }
              BulkDefinitionExecutable.apply(
                client,
                ElasticDsl.bulk(indexReqs)
              ) flatMap (_ => _scroll(response.scrollId))
            } else {
              Future.successful(())
            }
          }
        }

        val scrollId = response.scrollId
        _scroll(scrollId)
      }
    }
  }

  def reindex(sourceIndex: String, targetIndex: String)(implicit executor: ExecutionContext) = {
    ReindexDefinition(sourceIndex, targetIndex)
  }
}
