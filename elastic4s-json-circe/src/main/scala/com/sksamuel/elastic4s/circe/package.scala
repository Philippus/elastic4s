package com.sksamuel.elastic4s

import io.circe._
import io.circe.jawn._

import scala.annotation.implicitNotFound
import scala.util.{Failure, Success}

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
    "No Decoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Decoder instance "
  )
  implicit def hitReaderWithCirce[T](implicit decoder: Decoder[T]): HitReader[T] =
    (hit: Hit) => decode[T](hit.sourceAsString).fold(Failure(_), Success(_))

  @implicitNotFound(
    "No Encoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Encoder instance "
  )
  implicit def indexableWithCirce[T](implicit encoder: Encoder[T],
                                     printer: Json => String = Printer.noSpaces.print): Indexable[T] =
    (t: T) => printer(encoder(t))

  @implicitNotFound(
    "No Decoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Decoder instance "
  )
  implicit def aggReaderWithCirce[T](implicit encoder: Decoder[T]): AggReader[T] =
    (json: String) => decode[T](json).fold(Failure(_), Success(_))

  @implicitNotFound(
    "No Encoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Encoder instance "
  )
  implicit def paramSerializerWithCirce[T](implicit encoder: Encoder[T],
                                     printer: Json => String = Printer.noSpaces.print): ParamSerializer[T] =
    (t: T) => printer(encoder(t))
}
