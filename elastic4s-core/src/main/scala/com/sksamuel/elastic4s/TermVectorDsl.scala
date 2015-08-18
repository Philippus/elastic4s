package com.sksamuel.elastic4s

import org.elasticsearch.action.termvector.{TermVectorRequestBuilder, TermVectorResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait TermVectorDsl {

  class TermVectorExecutable extends Executable[TermVectorDefinition, TermVectorResponse, TermVectorResponse] {
    override def apply(client: Client, t: TermVectorDefinition): Future[TermVectorResponse] = {
      injectFuture(t.build(client.prepareTermVector).execute)
    }
  }
}

case class TermVectorDefinition(index: String,
                                `type`: String,
                                id: String,
                                positions: Option[Boolean] = None,
                                payloads: Option[Boolean] = None,
                                offsets: Option[Boolean] = None,
                                routing: Option[String] = None,
                                termStatistics: Option[Boolean] = None,
                                fieldStatistics: Option[Boolean] = None,
                                fields: Option[Seq[String]] = None) {

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
    fields.foreach(builder.setSelectedFields)
    builder
  }

  def termStatistics(b: Boolean) = copy(termStatistics = Option(b))
  def fieldStatistics(b: Boolean) = copy(fieldStatistics = Option(b))
  def fields(fields: String*) = copy(fields = Option(fields))
  def routing(routing: String) = copy(routing = Option(routing))
  def offsets(offsets: Boolean) = copy(offsets = Option(offsets))
  def payloads(payloads: Boolean) = copy(payloads = Option(payloads))
  def positions(positions: Boolean) = copy(positions = Option(positions))
}