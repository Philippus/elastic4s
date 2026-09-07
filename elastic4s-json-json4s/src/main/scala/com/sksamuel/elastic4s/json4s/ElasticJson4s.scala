package com.sksamuel.elastic4s.json4s

import com.sksamuel.elastic4s.{AggReader, Hit, HitReader, Indexable, ParamSerializer}
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods.{compact, render}
import scala.util.Try

object ElasticJson4s {
  object Implicits {

    implicit def Json4sHitReader[T](implicit json4s: Serialization, formats: Formats, mf: Manifest[T]): HitReader[T] =
      (hit: Hit) =>
        Try {
          val source = json4s.read[JObject](hit.sourceAsString)
          val withId = if ((source \ "_id") == JNothing) source ~ ("_id" -> hit.id) else source
          json4s.read[T](compact(render(withId)))
        }

    implicit def Json4sAggReader[T](implicit json4s: Serialization, formats: Formats, mf: Manifest[T]): AggReader[T] =
      (json: String) =>
        Try {
          json4s.read[T](json)
        }

    implicit def Json4sIndexable[T <: AnyRef](implicit json4s: Serialization, formats: Formats): Indexable[T] =
      (t: T) => json4s.write(t)

    implicit def Json4sParamSerializer[T <: AnyRef](implicit
        json4s: Serialization,
        formats: Formats
    ): ParamSerializer[T] =
      (t: T) => json4s.write(t)
  }
}
