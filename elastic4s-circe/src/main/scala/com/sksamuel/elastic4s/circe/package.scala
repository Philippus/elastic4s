package com.sksamuel.elastic4s

import io.circe._
import io.circe.jawn._
import io.circe.syntax._

import com.sksamuel.elastic4s.source.Indexable
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
  
  @implicitNotFound("No Decoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Decoder instance ")
  implicit def hitAsWithCirce[T](
    implicit decoder: Decoder[T]      
  ): HitAs[T] = new HitAs[T] {
    override def as(hit: RichSearchHit): T = decode[T](hit.sourceAsString)
      .getOrElse(throw new IllegalArgumentException(s"Unable to parse ${hit.sourceAsString}"))
  }
  
  @implicitNotFound("No Encoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Encoder instance ")
  implicit def indexableWithCirce[T](
    implicit encoder: Encoder[T]      
  ): Indexable[T] = new Indexable[T] {
    override def json(t: T): String = encoder(t).noSpaces
  }
}
