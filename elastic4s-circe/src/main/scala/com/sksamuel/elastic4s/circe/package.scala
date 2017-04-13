package com.sksamuel.elastic4s

import io.circe._
import io.circe.jawn._

import scala.annotation.implicitNotFound

/**
 * Automatic HitAs and Indexable derivation
 *
 * == Usage ==
 *
 * {{{
 *  import io.circe.generic.auto._
 *  import com.sksamuel.elastic4s.circe._
 *
 *  case class City(id: Int, name: String)
 *
 *  // index
 *  index into "places" / "cities" id cityId source City(1, "munich")
 *
 *  // search and parse
 *  val resp = client.execute {
 *    search in "places" / "cities"
 *  }.await
 *
 *  val cities = resp.as[City]
 *
 * }}}
 */
package object circe {

  @implicitNotFound(
    "No Decoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Decoder instance ")
  implicit def hitReaderWithCirce[T](implicit decoder: Decoder[T]): HitReader[T] = new HitReader[T] {
    override def read(hit: Hit): Either[Throwable, T] = decode[T](hit.sourceAsString)
  }

  @implicitNotFound(
    "No Encoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Encoder instance ")
  implicit def indexableWithCirce[T](implicit encoder: Encoder[T], printer: Json => String = Printer.noSpaces.pretty): Indexable[T] = new Indexable[T] {
    override def json(t: T): String = printer(encoder(t))
  }
}
