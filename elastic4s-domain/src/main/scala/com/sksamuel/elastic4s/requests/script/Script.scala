package com.sksamuel.elastic4s.requests.script

import com.sksamuel.elastic4s.ParamSerializer

import scala.language.implicitConversions

/** A script that can be used in queries (ex: Painless scripting).
  * @param script
  *   the script source
  * @param lang
  *   the language of the script (defaults to Painless)
  * @param scriptType
  *   the type of script (see [[ScriptType.Source]])
  * @param params
  *   the parameters of the script which will be automatically-serialized
  * @param options
  *   the options for this script
  * @param paramsRaw
  *   the parameters of the script which are already serialized
  */
case class Script(
    script: String,
    lang: Option[String] = None,
    scriptType: ScriptType = ScriptType.Source,
    params: Map[String, Any] = Map.empty,
    options: Map[String, String] = Map.empty,
    paramsRaw: Map[String, String] = Map.empty
) {

  /** Sets the script language.
    */
  def lang(lang: String): Script = copy(lang = Option(lang))

  /** Sets a parameter that will be automatically-serialized before sending to Elasticsearch.
    * @param name
    *   the parameter key
    * @param value
    *   the parameter value as a primitive type ([[Double]], [[BigInt]], [[Seq]], [[Map]], etc.); for all the primitive
    *   types supported, see [[com.sksamuel.elastic4s.json.XContentBuilder#autofield]]; for sending objects, see
    *   [[paramRaw]] and [[paramObject]]
    */
  def param(name: String, value: Any): Script                       = copy(params = params + (name -> value))
  def params(first: (String, Any), rest: (String, AnyRef)*): Script = params(first +: rest)
  def params(seq: Seq[(String, Any)]): Script                       = params(seq.toMap)
  def params(map: Map[String, Any]): Script                         = copy(params = params ++ map)

  /** Sets an already-serialized parameter.
    * @param name
    *   the parameter key
    * @param value
    *   the parameter serialized value as a string
    */
  def paramRaw(name: String, value: String): Script                       = copy(paramsRaw = paramsRaw + (name -> value))
  def paramsRaw(first: (String, String), rest: (String, String)*): Script = paramsRaw(first +: rest)
  def paramsRaw(seq: Seq[(String, String)]): Script                       = paramsRaw(seq.toMap)
  def paramsRaw(map: Map[String, String]): Script                         = copy(paramsRaw = paramsRaw ++ map)

  /** Sets a parameter with an object value, which is serialized with the given implicit serializer.
    * @param name
    *   the parameter key
    * @param value
    *   the parameter value
    * @param serializer
    *   the parameter serializer (see [[ParamSerializer]])
    * @tparam T
    *   object's type
    */
  def paramObject[T](name: String, value: T)(implicit serializer: ParamSerializer[T]): Script                  =
    copy(paramsRaw = paramsRaw + (name -> serializer.json(value)))
  def paramsObject[T](first: (String, T), rest: (String, T)*)(implicit serializer: ParamSerializer[T]): Script =
    paramsObject(first +: rest)
  def paramsObject[T](seq: Seq[(String, T)])(implicit serializer: ParamSerializer[T]): Script                  = paramsObject(seq.toMap)
  def paramsObject[T](map: Map[String, T])(implicit serializer: ParamSerializer[T]): Script                    =
    copy(paramsRaw = paramsRaw ++ map.mapValues(serializer.json))

  def scriptType(tpe: String): Script            = copy(scriptType = ScriptType.valueOf(tpe))
  def scriptType(scriptType: ScriptType): Script = copy(scriptType = scriptType)
}

object Script {
  implicit def string2Script(script: String): Script = Script(script)
}
