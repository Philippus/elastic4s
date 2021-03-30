package com.sksamuel.elastic4s.json4s

import com.sksamuel.elastic4s.{AggReader, Hit, HitReader, Indexable, ParamSerializer}
import org.json4s._

import scala.reflect.Manifest
import scala.util.Try

object ElasticJson4s {
  object Implicits {

    implicit def Json4sHitReader[T](implicit json4s: Serialization, formats: Formats, mf: Manifest[T]): HitReader[T] =
      (hit: Hit) => Try {
        json4s.read[T](hit.sourceAsString)
      }

    implicit def Json4sAggReader[T](implicit json4s: Serialization, formats: Formats, mf: Manifest[T]): AggReader[T] =
      (json: String) => Try {
        json4s.read[T](json)
      }

    implicit def Json4sIndexable[T <: AnyRef](implicit json4s: Serialization, formats: Formats): Indexable[T] =
      (t: T) => json4s.write(t)

    implicit def Json4sParamSerializer[T <: AnyRef](implicit json4s: Serialization, formats: Formats): ParamSerializer[T] =
      (t: T) => json4s.write(t)
  }
}
