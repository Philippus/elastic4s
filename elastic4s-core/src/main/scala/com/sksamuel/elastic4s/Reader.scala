package com.sksamuel.elastic4s

import org.scalactic.{Good, Bad, One, Or, Every, ErrorMessage}

@deprecated("use HitReader, this reader trait has a broken contravariance implementation", "1.6.1")
trait Reader[-U] {
  def read[T <: U : Manifest](json: String): T
}

@deprecated("use HitReader which handles errors", "2.0.0")
trait HitAs[T] {
  def as(hit: RichSearchHit): T
}

trait HitReader[T] {
  def as(hit: RichSearchHit): T Or Every[ErrorMessage]
}

trait HitFieldReader[T] {
  def as(prefix: String)(hit: RichSearchHitField): T Or Every[ErrorMessage]
  def as(hit: RichSearchHitField): T Or Every[ErrorMessage] = as("")(hit)
}

object HitFieldReader {

  //  implicit object IntHitFieldReader extends HitFieldReader[Int] {
  //    override def as(prefix: String)(hit: RichSearchHitField): Int Or Every[ErrorMessage] = hit match {
  //      case MissingRichSearchField(name) => Bad(One(s"$prefix $name field is missing"))
  //      case field => field.value[Any] match {
  //        case v: Int => Good(v)
  //        case v: Long => Good(v.toInt)
  //        case v: BigInt => Good(v.intValue())
  //        case o => Bad(One(s"$prefix ${hit.name} expected an Int got $o"))
  //      }
  //    }
  //  }
  //
  //  implicit object BigDecimalHitFieldReader extends HitFieldReader[BigDecimal] {
  //    override def as(prefix: String)(hit: RichSearchHitField): BigDecimal Or Every[ErrorMessage] = hit match {
  //      case MissingRichSearchField(name) => Bad(One(s"$prefix $name field is missing"))
  //      case field => field.value[Any] match {
  //        case v: Int => Good(BigDecimal(v))
  //        case v: Long => Good(BigDecimal(v))
  //        case v: BigInt => Good(BigDecimal(v))
  //        case v: Double => Good(BigDecimal(v))
  //        case v: Float => Good(BigDecimal(v))
  //        case v: BigDecimal => Good(v)
  //        case v: java.math.BigDecimal => Good(v)
  //        case v: java.math.BigInteger => Good(BigDecimal(v.longValue()))
  //        case o => Bad(One(s"$prefix ${hit.name} expected an Int got $o"))
  //      }
  //    }
  //  }

  //  implicit def optionReader[T](implicit reader: HitFieldReader[T]): HitFieldReader[Option[T]] = {
  //    new HitFieldReader[Option[T]] {
  //      override def as(prefix: String)(hit: RichSearchHitField): Option[T] Or Every[ErrorMessage] = hit match {
  //        case MissingRichSearchField(name) => Good(None)
  //        case field => reader.as(prefix)(hit).map(Some.apply)
  //      }
  //    }
  //  }
}