package com.sksamuel.elastic4s.requests.analyzers

@deprecated("use new analysis package", "7.0.1")
abstract class Normalizer(val name: String)

@deprecated("use new analysis package", "7.0.1")
case class CustomNormalizer(override val name: String) extends Normalizer(name)
