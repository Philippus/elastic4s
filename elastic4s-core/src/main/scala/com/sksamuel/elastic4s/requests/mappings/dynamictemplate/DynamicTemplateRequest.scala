package com.sksamuel.elastic4s.requests.mappings.dynamictemplate

import com.sksamuel.elastic4s.requests.mappings.FieldDefinition
import com.sksamuel.exts.OptionImplicits._

case class DynamicTemplateRequest(
  name: String,
  mapping: FieldDefinition, // definition of the field, elasticsearch calls this the mapping
  `match`: Option[String] = None,
  unmatch: Option[String] = None,
  pathMatch: Option[String] = None,
  pathUnmatch: Option[String] = None,
  MatchPattern: Option[String] = None,
  matchMappingType: Option[String] = None
) {

  def `match`(m: String): DynamicTemplateRequest      = matching(m)
  def matching(m: String): DynamicTemplateRequest     = copy(`match` = m.some)
  def matchPattern(m: String): DynamicTemplateRequest = copy(MatchPattern = m.some)

  def unmatch(m: String): DynamicTemplateRequest        = copy(unmatch = m.some)
  def pathMatch(path: String): DynamicTemplateRequest   = copy(pathMatch = path.some)
  def pathUnmatch(path: String): DynamicTemplateRequest = copy(pathUnmatch = path.some)

  def matchMappingType(`type`: String): DynamicTemplateRequest = copy(matchMappingType = `type`.some)
}
