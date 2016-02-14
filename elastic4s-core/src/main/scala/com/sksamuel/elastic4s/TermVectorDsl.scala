package com.sksamuel.elastic4s

import org.elasticsearch.action.termvector.{TermVectorRequestBuilder, TermVectorResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait TermVectorDsl {

  def termVector(index: String, `type`: String, id: String) = TermVectorDefinition(index, `type`, id)

  implicit object TermVectorExecutable
    extends Executable[TermVectorDefinition, TermVectorResponse, TermVectorResponse] {
    override def apply(client: Client, t: TermVectorDefinition): Future[TermVectorResponse] = {
      injectFuture(t.build(client.prepareTermVector).execute)
    }
  }
}

case class TermVectorDefinition(private val index: String,
                                private val `type`: String,
                                private val id: String,
                                private val positions: Option[Boolean] = None,
                                private val payloads: Option[Boolean] = None,
                                private val offsets: Option[Boolean] = None,
                                private val routing: Option[String] = None,
                                private val termStatistics: Option[Boolean] = None,
                                private val fieldStatistics: Option[Boolean] = None,
                                private val fields: Option[Seq[String]] = None) {

  def build(builder: TermVectorRequestBuilder): TermVectorRequestBuilder = {
    builder.setIndex(index)
    builder.setType(`type`)
    builder.setId(id)
    termStatistics.foreach(builder.setTermStatistics)
    fieldStatistics.foreach(builder.setFieldStatistics)
    positions.foreach(builder.setPositions)
    payloads.foreach(builder.setPayloads)
    offsets.foreach(builder.setOffsets)
    routing.foreach(builder.setRouting)
    fields.foreach(flds => builder.setSelectedFields(flds: _ *))
    builder
  }

  def withTermStatistics(boolean: Boolean = true): TermVectorDefinition = copy(termStatistics = Option(boolean))
  def withFieldStatistics(boolean: Boolean = true): TermVectorDefinition = copy(fieldStatistics = Option(boolean))
  def withFields(fields: String*): TermVectorDefinition = copy(fields = Option(fields))
  def withRouting(routing: String): TermVectorDefinition = copy(routing = Option(routing))
  def withOffets(boolean: Boolean = true): TermVectorDefinition = copy(offsets = Option(boolean))
  def withPayloads(boolean: Boolean = true): TermVectorDefinition = copy(payloads = Option(boolean))
  def withPositions(boolean: Boolean = true): TermVectorDefinition = copy(positions = Option(boolean))
}