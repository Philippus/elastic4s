package com.sksamuel.elastic4s

import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptService.{ScriptType => ESScriptType}

import scala.language.implicitConversions

trait ScriptDsl {
  def script(script: String): ScriptDefinition = ScriptDefinition(null, script)
  def script(name: String, script: String) = ScriptDefinition(name, script)
}

case class ScriptDefinition(name: String, // todo is this actually used?
                            script: String,
                            lang: Option[String] = None,
                            scriptType: ESScriptType = ESScriptType.INLINE,
                            params: Map[String, Any] = Map.empty) {

  import scala.collection.JavaConverters._

  def lang(lang: String): ScriptDefinition = copy(lang = Option(lang))
  def param(name: String, value: Any): ScriptDefinition = copy(params = params + (name -> value))
  def params(first: (String, Any), rest: (String, Any)*): ScriptDefinition = params(first +: rest)
  def params(seq: Seq[(String, Any)]): ScriptDefinition = params(seq.toMap)
  def params(map: Map[String, Any]): ScriptDefinition = copy(params = params ++ map)
  def scriptType(scriptType: ESScriptType): ScriptDefinition = copy(scriptType = scriptType)

  def toJavaAPI: Script = new Script(script, scriptType, lang.orNull, params.asJava)
}

object ScriptDefinition {
  implicit def string2Script(script: String): ScriptDefinition = ScriptDefinition(null, script)
}