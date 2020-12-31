package com.sksamuel.elastic4s.requests.indexes

/**
 *
 * GET /_analyze
 * POST /_analyze
 * GET /<index>/_analyze
 * POST /<index>/_analyze
 *
 * @param text
 *             (Required, string or array of strings) Text to analyze.
 *             If an array of strings is provided, it is analyzed as a multi-value field.
 *
 * @param analyzer (Optional, string) The name of the analyzer that should be applied to the provided text.
 *             If this parameter is not specified, the analyze API uses the analyzer defined in the field’s mapping.
 *             If no field is specified, the analyze API uses the default analyzer for the index.
 *             If no index is specified, or the index does not have a default analyzer, the analyze API uses the standard analyzer.
 *
 * @param index (Optional, string) Index used to derive the analyzer.
 *             If specified, the analyzer or <field> parameter overrides this value.
 *             If no analyzer or field are specified, the analyze API uses the default analyzer for the index.
 *             If no index is specified or the index does not have a default analyzer, the analyze API uses the standard analyzer.
 */
case class AnalyzeResponse(text:String,
                           analyzer:Option[String] = None,
                           index:Option[String] = None,
                           attributes:Seq[String] = Seq.empty) {

  def analyzer(name:String): AnalyzeResponse = copy(analyzer = Some(name))
  def index(name:String): AnalyzeResponse    = copy(index = Some(name))

  def attributes(attrs:Seq[String]): AnalyzeResponse = copy(attributes = attrs)

}
