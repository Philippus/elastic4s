package com.sksamuel.elastic4s.requests.indexes.analyze

import com.fasterxml.jackson.annotation.JsonProperty

case class AnalyseToken(token: String,
                        @JsonProperty("start_offset") startOffset: Int,
                        @JsonProperty("end_offset") endOffset: Int,
                        @JsonProperty("type") tokenType: String,
                        position: Int,
                        @JsonProperty("bytes") hexBytes: Option[String] = None,
                        positionLength: Option[Int] = None,
                        termFrequency: Option[Int] = None,
                        keyword: Option[Boolean] = None)


case class ExplainAnalyzeDetail(@JsonProperty("custom_analyzer") customAnalyzer: Boolean,
                                analyzer: Option[ExplainAnalyzer] = None,
                                @JsonProperty("tokenfilters") tokenFilters: Seq[ExplainTokenFilters] = Seq.empty)


case class ExplainAnalyzer(name: String, tokens: Seq[AnalyseToken])

case class ExplainTokenFilters(name: String, tokens: Seq[AnalyseToken])


trait AnalyzeResponse
/**
 * response of analyzerRequest with explain = false
 */
case class NoExplainAnalyzeResponse(@JsonProperty("tokens") tokens: Seq[AnalyseToken]) extends AnalyzeResponse

/**
 * response of analyzerRequest with explain = true
 */
case class ExplainAnalyzeResponse(detail: ExplainAnalyzeDetail) extends AnalyzeResponse
