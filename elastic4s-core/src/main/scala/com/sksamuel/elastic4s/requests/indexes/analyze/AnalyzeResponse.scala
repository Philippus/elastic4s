package com.sksamuel.elastic4s.requests.indexes.analyze

import com.fasterxml.jackson.annotation.JsonProperty


trait AnalyzeResponse

/**
 * response of analyzerRequest with explain = false
 */
case class NoExplainAnalyzeResponse(@JsonProperty("tokens") tokens: Seq[NoExplainToken]) extends AnalyzeResponse

case class NoExplainToken(token: String,
                          @JsonProperty("start_offset") startOffset: Int,
                          @JsonProperty("end_offset") endOffset: Int,
                          @JsonProperty("type") tokenType: String,
                          position: Int)


/**
 * response of analyzerRequest with explain = true
 */
case class ExplainAnalyzeResponse(detail:ExplainAnalyzeDetail)  extends AnalyzeResponse

case class ExplainToken(token: String,
                       @JsonProperty("start_offset") startOffset: Int,
                       @JsonProperty("end_offset") endOffset: Int,
                       @JsonProperty("type") tokenType: String,
                       position: Int,
                       @JsonProperty("bytes") hexBytes:String,
                       positionLength:Int,
                       termFrequency:Int)

case class ExplainAnalyzer(name: String, tokens: Seq[ExplainToken])

case class ExplainAnalyzeDetail(@JsonProperty("custom_analyzer") customAnalyzer: Boolean,
                         analyzer: ExplainAnalyzer)
