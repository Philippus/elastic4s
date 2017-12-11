package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.script.{ScriptDefinition, ScriptType}
import org.elasticsearch.script.Script

import scala.collection.JavaConverters._

object ScriptBuilder {

  import EnumConversions._

  def apply(script: ScriptDefinition): Script = {
    var options = script.options.asJava
    if (script.scriptType != com.sksamuel.elastic4s.script.ScriptType.Inline) {
      options = null
    }
    val lang = if (script.scriptType == ScriptType.Stored) null else script.lang.getOrElse(Script.DEFAULT_SCRIPT_LANG)
    if (script.params.isEmpty) {
      new Script(
        script.scriptType,
        lang,
        script.script,
        options,
        new java.util.HashMap[String, Object]()
      )
    } else {
      val mappedParams = FieldsMapper.mapper(script.params).asJava
      new Script(
        script.scriptType,
        lang,
        script.script,
        options,
        mappedParams
      )
    }
  }
}
