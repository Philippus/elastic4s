package com.sksamuel.elastic4s

import org.scalatest.{FunSuite, Matchers}
import scala.collection.JavaConverters._

class FieldsMapperTest extends FunSuite with Matchers {

  test("support head nulls in collections") {
    val expected = Map("Things" -> Array[Object](null, Map("Color" -> "red").asJava))
    val actual = FieldsMapper.mapper(Map("Things" -> Seq(null, Map("Color" -> "red"))))
    actual("Things").asInstanceOf[Array[Object]].toList shouldBe expected("Things").toList
  }

  test("support tail nulls in collections") {
    val expected = Map("Things" -> Array[Object](Map("Color" -> "red").asJava, null))
    val actual = FieldsMapper.mapper(Map("Things" -> Seq(Map("Color" -> "red"), null)))
    actual("Things").asInstanceOf[Array[Object]].toList shouldBe expected("Things").toList
  }

  test("support mixed nulls in collections") {
    val expected = Map("Things" -> Array[Object](null, Map("Color" -> "red").asJava, null))
    val actual = FieldsMapper.mapper(Map("Things" -> Seq(null, Map("Color" -> "red"), null)))
    actual("Things").asInstanceOf[Array[Object]].toList shouldBe expected("Things").toList
  }
}
