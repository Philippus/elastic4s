package com.sksamuel.elastic4s.requests.mappings.dynamictemplate

import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.fields.ElasticField

case class DynamicTemplateRequest(
    name: String,
    mapping: ElasticField,
    `match`: Seq[String] = Nil,
    unmatch: Seq[String] = Nil,
    pathMatch: Seq[String] = Nil,
    pathUnmatch: Seq[String] = Nil,
    matchPattern: Option[String] = None,
    matchMappingType: Seq[String] = Nil,
    unmatchMappingType: Seq[String] = Nil
) {

  def `match`(m: String): DynamicTemplateRequest            = matching(m)
  def `match`(ms: Iterable[String]): DynamicTemplateRequest = copy(`match` = ms.toSeq)
  def matching(m: String): DynamicTemplateRequest           = copy(`match` = Seq(m))
  def matchPattern(m: String): DynamicTemplateRequest       = copy(matchPattern = m.some)

  def unmatch(u: String): DynamicTemplateRequest                   = copy(unmatch = Seq(u))
  def unmatch(us: Iterable[String]): DynamicTemplateRequest        = copy(unmatch = us.toSeq)
  def pathMatch(path: String): DynamicTemplateRequest              = copy(pathMatch = Seq(path))
  def pathMatch(paths: Iterable[String]): DynamicTemplateRequest   = copy(pathMatch = paths.toSeq)
  def pathUnmatch(path: String): DynamicTemplateRequest            = copy(pathUnmatch = Seq(path))
  def pathUnmatch(paths: Iterable[String]): DynamicTemplateRequest = copy(pathUnmatch = paths.toSeq)

  def matchMappingType(`type`: String): DynamicTemplateRequest            = copy(matchMappingType = Seq(`type`))
  def matchMappingType(types: Iterable[String]): DynamicTemplateRequest   = copy(matchMappingType = types.toSeq)
  def unmatchMappingType(`type`: String): DynamicTemplateRequest          = copy(unmatchMappingType = Seq(`type`))
  def unmatchMappingType(types: Iterable[String]): DynamicTemplateRequest = copy(unmatchMappingType = types.toSeq)
}

object DynamicTemplateRequest {
  @deprecated("Use the new apply method", "9.4.0") def apply(
      name: String,
      mapping: ElasticField,
      `match`: Option[String],
      unmatch: Option[String],
      pathMatch: Option[String],
      pathUnmatch: Option[String],
      MatchPattern: Option[String],
      matchMappingType: Option[String]
  ): DynamicTemplateRequest =
    new DynamicTemplateRequest(
      name,
      mapping,
      `match`.toSeq,
      unmatch.toSeq,
      pathMatch.toSeq,
      pathUnmatch.toSeq,
      MatchPattern,
      matchMappingType.toSeq
    )
}
