package com.sksamuel.elastic4s.json4s

import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.{HitAs, RichSearchHit}
import org.json4s._

import scala.reflect.Manifest

object ElasticJson4s {
  object Implicits {

    implicit def Json4sHitAs[T](implicit json4s: Serialization,
                                formats: Formats,
                                mf: Manifest[T]): HitAs[T] = new HitAs[T] {
      override def as(hit: RichSearchHit): T = json4s.read[T](hit.sourceAsString)
    }

    implicit def Json4sIndexable[T <: AnyRef](implicit json4s: Serialization,
                                              formats: Formats): Indexable[T] = new Indexable[T] {
      override def json(t: T): String = json4s.write(t)
    }
  }
}