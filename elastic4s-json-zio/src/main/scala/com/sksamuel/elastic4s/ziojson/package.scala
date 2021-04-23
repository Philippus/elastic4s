package com.sksamuel.elastic4s

import zio.json._

import scala.annotation.implicitNotFound

package object ziojson {
  @implicitNotFound("No JsonEncoder for type ${T} found. Bring an implicit JsonEncoder[T] instance in scope")
  implicit def zioJsonIndexable[T](implicit encoder: JsonEncoder[T]): Indexable[T] = _.toJson

  @implicitNotFound("No JsonDecoder for type ${T} found. Bring an implicit ReadsJsonDecoder[T] instance in scope")
  implicit def zioJsonHitReader[T](implicit decoder: JsonDecoder[T]): HitReader[T] =
    _.sourceAsString.fromJson[T].left.map(new Throwable(_)).toTry

  @implicitNotFound("No JsonDecoder for type ${T} found. Bring an implicit ReadsJsonDecoder[T] instance in scope")
  implicit def zioJsonAggReader[T](implicit decoder: JsonDecoder[T]): AggReader[T] =
    _.fromJson[T].left.map(new Throwable(_)).toTry

  @implicitNotFound("No JsonEncoder for type ${T} found. Bring an implicit JsonEncoder[T] instance in scope")
  implicit def zioJsonParamSerializer[T](implicit encoder: JsonEncoder[T]): ParamSerializer[T] = _.toJson
}
