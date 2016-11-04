package com.sksamuel.elastic4s.termvectors

import com.sksamuel.elastic4s.{Executable, IndexAndType}
import org.elasticsearch.action.termvectors.TermVectorsResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait TermVectorDsl {

  def termVectors(index: String, `type`: String, id: String) = TermVectorsDefinition(IndexAndType(index, `type`), id)

  implicit object TermVectorExecutable
    extends Executable[TermVectorsDefinition, TermVectorsResponse, TermVectorsResult] {
    override def apply(client: Client, t: TermVectorsDefinition): Future[TermVectorsResult] = {
      val builder = client.prepareTermVectors(t.indexAndType.index, t.indexAndType.`type`, t.id)
      t.populate(builder)
      injectFutureAndMap(builder.execute)(TermVectorsResult.apply)
    }
  }
}