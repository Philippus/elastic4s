package com.sksamuel.elastic4s.requests.termvectors

import com.sksamuel.elastic4s.Index
import com.sksamuel.exts.OptionImplicits._

case class TermVectorsRequest(index: Index,
                              `type`: Option[String],
                              id: String,
                              fieldStatistics: Option[Boolean] = None,
                              offsets: Option[Boolean] = None,
                              parent: Option[String] = None,
                              payloads: Option[Boolean] = None,
                              positions: Option[Boolean] = None,
                              preference: Option[String] = None,
                              realtime: Option[Boolean] = None,
                              routing: Option[String] = None,
                              fields: Seq[String] = Nil,
                              termStatistics: Option[Boolean] = None,
                              version: Option[Long] = None,
                              versionType: Option[String] = None,
                              maxNumTerms: Option[Int] = None,
                              minTermFreq: Option[Int] = None,
                              maxTermFreq: Option[Int] = None,
                              minDocFreq: Option[Int] = None,
                              maxDocFreq: Option[Int] = None,
                              minWordLength: Option[Int] = None,
                              maxWordLength: Option[Int] = None,
                              perFieldAnalyzer: Map[String, String] = Map.empty) {

  def field(fieldName: String): TermVectorsRequest = fields(Seq(fieldName))
  def fields(fields: Iterable[String]): TermVectorsRequest = copy(fields = fields.toSeq)
  def fields(fields: String*): TermVectorsRequest = copy(fields = fields.toSeq)

  def fieldStatistics(boolean: Boolean): TermVectorsRequest = copy(fieldStatistics = Option(boolean))
  def offsets(boolean: Boolean): TermVectorsRequest         = copy(offsets = Option(boolean))
  def parent(str: String): TermVectorsRequest               = copy(parent = Option(str))
  def payloads(boolean: Boolean): TermVectorsRequest        = copy(payloads = Option(boolean))
  def positions(boolean: Boolean): TermVectorsRequest       = copy(positions = Option(boolean))
  def preference(str: String): TermVectorsRequest           = copy(preference = Option(str))
  def realtime(boolean: Boolean): TermVectorsRequest        = copy(realtime = Option(boolean))
  def routing(str: String): TermVectorsRequest              = copy(routing = Option(str))
  def termStatistics(boolean: Boolean): TermVectorsRequest  = copy(termStatistics = Option(boolean))
  def version(version: Long): TermVectorsRequest            = copy(version = Option(version))
  def versionType(versionType: String): TermVectorsRequest  = copy(versionType = versionType.some)

  def perFieldAnalyzer(perFieldAnalyzer: Map[String, String]): TermVectorsRequest =
    copy(perFieldAnalyzer = perFieldAnalyzer)

  def maxNumTerms(maxNumTerms: Int): TermVectorsRequest     = copy(maxNumTerms = Option(maxNumTerms))
  def minTermFreq(minTermFreq: Int): TermVectorsRequest     = copy(minTermFreq = Option(minTermFreq))
  def maxTermFreq(maxTermFreq: Int): TermVectorsRequest     = copy(maxTermFreq = Option(maxTermFreq))
  def minDocFreq(minDocFreq: Int): TermVectorsRequest       = copy(minDocFreq = Option(minDocFreq))
  def maxDocFreq(maxDocFreq: Int): TermVectorsRequest       = copy(maxDocFreq = Option(maxDocFreq))
  def minWordLength(minWordLength: Int): TermVectorsRequest = copy(minWordLength = Option(minWordLength))
  def maxWordLength(maxWordLength: Int): TermVectorsRequest = copy(maxWordLength = Option(maxWordLength))
}
