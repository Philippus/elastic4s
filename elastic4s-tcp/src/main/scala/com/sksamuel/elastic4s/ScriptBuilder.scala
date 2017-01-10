package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.script.{Script, ScriptType}

import scala.collection.JavaConverters._

object ScriptBuilder {
  def apply(script: ScriptDefinition): Script = {
    if (script.params.isEmpty) {
      new Script(
        ScriptType.valueOf(script.scriptType.toUpperCase),
        script.lang.getOrElse(Script.DEFAULT_SCRIPT_LANG),
        script.script,
        script.options.asJava,
        new java.util.HashMap[String, Object]()
      )
    } else {
      val mappedParams = FieldsMapper.mapper(script.params).asJava
      new Script(
        ScriptType.valueOf(script.scriptType.toUpperCase),
        script.lang.getOrElse(Script.DEFAULT_SCRIPT_LANG),
        script.script,
        script.options.asJava,
        mappedParams
      )
    }
  }
}
