package com.sksamuel.elastic4s.termvectors

import org.elasticsearch.action.termvectors.TermVectorsResponse
import scala.concurrent.duration._

case class TermVectorsResult(original: TermVectorsResponse) {

  def fields = original.getFields
  def terms(fieldName: String) = fields.terms(fieldName)

  def id: String = original.getId
  def index: String = original.getIndex
  def took: FiniteDuration = original.getTookInMillis.millis
  def `type`: String = original.getType
  def version: Long = original.getVersion
  def artifical = original.isArtificial
  def exists = original.isExists
}
