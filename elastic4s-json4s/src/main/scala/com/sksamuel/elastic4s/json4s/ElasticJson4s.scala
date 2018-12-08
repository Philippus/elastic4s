package com.sksamuel.elastic4s.json4s

import com.sksamuel.elastic4s.{AggReader, Hit, HitReader, Indexable}
import org.json4s._

import scala.reflect.Manifest
import scala.util.Try

object ElasticJson4s {
  object Implicits {

    implicit def Json4sHitReader[T](implicit json4s: Serialization, formats: Formats, mf: Manifest[T]): HitReader[T] =
      new HitReader[T] {
        override def read(hit: Hit): Try[T] = Try {
          json4s.read[T](hit.sourceAsString)
        }
      }

    implicit def Json4sAggReader[T](implicit json4s: Serialization, formats: Formats, mf: Manifest[T]): AggReader[T] =
      new AggReader[T] {
        override def read(json: String): Try[T] = Try {
          json4s.read[T](json)
        }
      }

    implicit def Json4sIndexable[T <: AnyRef](implicit json4s: Serialization, formats: Formats): Indexable[T] =
      new Indexable[T] {
        override def json(t: T): String = json4s.write(t)
      }
  }
}
