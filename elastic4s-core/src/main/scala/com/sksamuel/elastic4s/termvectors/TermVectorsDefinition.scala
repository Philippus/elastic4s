package com.sksamuel.elastic4s.termvectors

import com.sksamuel.elastic4s.IndexAndType
import com.sksamuel.exts.OptionImplicits._

case class TermVectorsDefinition(indexAndType: IndexAndType,
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

  def fieldStatistics(boolean: Boolean): TermVectorsDefinition = copy(fieldStatistics = Option(boolean))
  def offsets(boolean: Boolean): TermVectorsDefinition = copy(offsets = Option(boolean))
  def parent(str: String): TermVectorsDefinition = copy(parent = Option(str))
  def payloads(boolean: Boolean): TermVectorsDefinition = copy(payloads = Option(boolean))
  def positions(boolean: Boolean): TermVectorsDefinition = copy(positions = Option(boolean))
  def preference(str: String): TermVectorsDefinition = copy(preference = Option(str))
  def realtime(boolean: Boolean): TermVectorsDefinition = copy(realtime = Option(boolean))
  def routing(str: String): TermVectorsDefinition = copy(routing = Option(str))
  def fields(fields: Iterable[String]): TermVectorsDefinition = copy(fields = fields.toSeq)
  def fields(fields: String*): TermVectorsDefinition = copy(fields = fields.toSeq)
  def termStatistics(boolean: Boolean): TermVectorsDefinition = copy(termStatistics = Option(boolean))
  def version(version: Long): TermVectorsDefinition = copy(version = Option(version))
  def versionType(versionType: String): TermVectorsDefinition = copy(versionType = versionType.some)

  def perFieldAnalyzer(perFieldAnalyzer: Map[String, String]): TermVectorsDefinition =
    copy(perFieldAnalyzer = perFieldAnalyzer)

  def maxNumTerms(maxNumTerms: Int): TermVectorsDefinition = copy(maxNumTerms = Option(maxNumTerms))
  def minTermFreq(minTermFreq: Int): TermVectorsDefinition = copy(minTermFreq = Option(minTermFreq))
  def maxTermFreq(maxTermFreq: Int): TermVectorsDefinition = copy(maxTermFreq = Option(maxTermFreq))
  def minDocFreq(minDocFreq: Int): TermVectorsDefinition = copy(minDocFreq = Option(minDocFreq))
  def maxDocFreq(maxDocFreq: Int): TermVectorsDefinition = copy(maxDocFreq = Option(maxDocFreq))
  def minWordLength(minWordLength: Int): TermVectorsDefinition = copy(minWordLength = Option(minWordLength))
  def maxWordLength(maxWordLength: Int): TermVectorsDefinition = copy(maxWordLength = Option(maxWordLength))
}
