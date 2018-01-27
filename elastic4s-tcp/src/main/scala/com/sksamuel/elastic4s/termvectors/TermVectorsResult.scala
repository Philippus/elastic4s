package com.sksamuel.elastic4s.termvectors

import org.apache.lucene.index.{Fields, Terms}
import org.elasticsearch.action.termvectors.TermVectorsResponse

import scala.concurrent.duration._

case class TermVectorsResult(original: TermVectorsResponse) {

  def fields: Fields                  = original.getFields
  def terms(fieldName: String): Terms = fields.terms(fieldName)

  def id: String           = original.getId
  def index: String        = original.getIndex
  def took: FiniteDuration = original.getTook.millis.millis
  def `type`: String       = original.getType
  def version: Long        = original.getVersion
  def artifical: Boolean   = original.isArtificial
  def exists: Boolean      = original.isExists
}
