package com.sksamuel.elastic4s.mappings.dynamictemplate

import com.sksamuel.elastic4s.mappings.FieldDefinition
import com.sksamuel.exts.OptionImplicits._

case class DynamicTemplateDefinition(name: String,
                                     mapping: FieldDefinition, // definition of the field, elasticsearch calls this the mapping
                                     `match`: Option[String] = None,
                                     unmatch: Option[String] = None,
                                     pathMatch: Option[String] = None,
                                     pathUnmatch: Option[String] = None,
                                     MatchPattern: Option[String] = None,
                                     matchMappingType: Option[String] = None) {

  def `match`(m: String): DynamicTemplateDefinition = matching(m)
  def matching(m: String): DynamicTemplateDefinition = copy(`match` = m.some)
  def matchPattern(m: String): DynamicTemplateDefinition = copy(MatchPattern = m.some)

  def unmatch(m: String): DynamicTemplateDefinition = copy(unmatch = m.some)
  def pathMatch(path: String): DynamicTemplateDefinition = copy(pathMatch = path.some)
  def pathUnmatch(path: String): DynamicTemplateDefinition = copy(pathUnmatch = path.some)

  def matchMappingType(`type`: String): DynamicTemplateDefinition = copy(matchMappingType = `type`.some)
}
