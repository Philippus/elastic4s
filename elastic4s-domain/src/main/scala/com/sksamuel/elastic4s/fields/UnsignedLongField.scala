package com.sksamuel.elastic4s.fields

import java.math.BigInteger

case class UnsignedLongField(name: String,
                             boost: Option[Double] = None,
                             coerce: Option[Boolean] = None,
                             copyTo: Seq[String] = Nil,
                             docValues: Option[Boolean] = None,
                             ignoreMalformed: Option[Boolean] = None,
                             scalingFactor: Option[Int] = None,
                             index: Option[Boolean] = None,
                             nullValue: Option[BigInteger] = None,
                             store: Option[Boolean] = None,
                             meta: Map[String, Any] = Map.empty) extends NumberField[BigInteger] {
  override def `type`: String = "unsigned_long"
}
