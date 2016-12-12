package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.FieldsMapper
import org.elasticsearch.script.{ Script, ScriptType }

import scala.collection.JavaConverters._
import scala.language.implicitConversions

case class ScriptDefinition(script: String,
                            lang: Option[String] = None,
                            scriptType: ScriptType = ScriptType.INLINE,
                            params: Map[String, Any] = Map.empty,
                            options: Map[String, String] = Map.empty) {

  def lang(lang: String): ScriptDefinition = copy(lang = Option(lang))
  def param(name: String, value: Any): ScriptDefinition = copy(params = params + (name -> value))
  def params(first: (String, Any), rest: (String, AnyRef)*): ScriptDefinition = params(first +: rest)
  def params(seq: Seq[(String, Any)]): ScriptDefinition = params(seq.toMap)
  def params(map: Map[String, Any]): ScriptDefinition = copy(params = params ++ map)

  def scriptType(scriptType: ScriptType): ScriptDefinition = copy(scriptType = scriptType)

  @deprecated("use build", "5.0.0")
  def toJavaAPI: Script = build

  def build: Script = {
    if (params.isEmpty) {
      new Script(scriptType, lang.getOrElse(Script.DEFAULT_SCRIPT_LANG), script,
                 options.asJava, Map.empty.asJava)
    } else {
      val mappedParams = FieldsMapper.mapper(params).asJava
      new Script(scriptType, lang.getOrElse(Script.DEFAULT_SCRIPT_LANG), script,
                 options.asJava, mappedParams)
    }
  }
}

object ScriptDefinition {
  implicit def string2Script(script: String): ScriptDefinition = ScriptDefinition(script)
}
