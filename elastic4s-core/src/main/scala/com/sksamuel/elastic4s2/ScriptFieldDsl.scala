package com.sksamuel.elastic4s2

import org.elasticsearch.script.ScriptService

trait ScriptFieldDsl {
  case class ExpectsScript(field: String) {
    def script(script: String): ScriptFieldDefinition = ScriptFieldDefinition(field, script, None, None)
  }
}

case class ScriptFieldDefinition(field: String,
                                 script: String,
                                 language: Option[String] = None,
                                 parameters: Option[Map[String, AnyRef]] = None,
                                 scriptType: ScriptService.ScriptType = ScriptService.ScriptType.INLINE) {
  def lang(l: String): ScriptFieldDefinition = copy(language = Option(l))
  def params(p: Map[String, Any]): ScriptFieldDefinition = {
    copy(parameters = Some(p.map(e => e._1 -> e._2.asInstanceOf[AnyRef])))
  }
  def params(ps: (String, Any)*): ScriptFieldDefinition = {
    copy(parameters = Some(ps.toMap.map(e => e._1 -> e._2.asInstanceOf[AnyRef])))
  }
  def scriptType(scriptType: ScriptService.ScriptType): ScriptFieldDefinition = copy(scriptType = scriptType)
}
