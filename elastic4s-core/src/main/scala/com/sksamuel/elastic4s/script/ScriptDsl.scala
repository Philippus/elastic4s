package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.FieldsMapper
import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptService.{ScriptType => ESScriptType}

import scala.language.implicitConversions

trait ScriptDsl {
  def script(script: String): ScriptDefinition = ScriptDefinition(script)
  def script(name: String, script: String) = ScriptDefinition(script)
}

case class ScriptDefinition(script: String,
                            lang: Option[String] = None,
                            scriptType: ESScriptType = ESScriptType.INLINE,
                            params: Map[String, Any] = Map.empty) {

  def lang(lang: String): ScriptDefinition = copy(lang = Option(lang))
  def param(name: String, value: Any): ScriptDefinition = copy(params = params + (name -> value))
  def params(first: (String, Any), rest: (String, AnyRef)*): ScriptDefinition = params(first +: rest)
  def params(seq: Seq[(String, Any)]): ScriptDefinition = params(seq.toMap)
  def params(map: Map[String, Any]): ScriptDefinition = copy(params = params ++ map)
  def scriptType(scriptType: ESScriptType): ScriptDefinition = copy(scriptType = scriptType)

  def toJavaAPI: Script = {
    if(params.isEmpty) {
      new Script(script, scriptType, lang.orNull, null)
    } else {
      val mappedParams = FieldsMapper.mapper(params).asJava
      new Script(script, scriptType, lang.orNull, mappedParams)
    }
  }
}

object ScriptDefinition {
  implicit def string2Script(script: String): ScriptDefinition = ScriptDefinition(script)
}
