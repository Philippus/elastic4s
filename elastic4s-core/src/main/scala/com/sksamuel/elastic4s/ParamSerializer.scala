package com.sksamuel.elastic4s

/**
  * A type class that is used to serialize the parameters sent to a [[com.sksamuel.elastic4s.requests.script.Script]].
  *
  * [[ParamSerializer]]s can be automatically derived if you add a module like `elastic4s-json-spray`.
  */
trait ParamSerializer[T] {
  def json(t: T): String
}
