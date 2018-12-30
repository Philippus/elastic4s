package com.sksamuel.elastic4s

object SourceAsContentBuilder {

  def apply(source: Map[String, Any]): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    def addMap(map: Map[String, Any]): Unit =
      map.foreach {
        case (key, value: Map[String, Any]) =>
          builder.startObject(key)
          addMap(value)
          builder.endObject()
        case (key, values: Seq[Any]) =>
          builder.startArray(key)
          addSeq(values)
          builder.endArray()
        case (key, value) =>
          value match {
            case x: String  => builder.field(key, x)
            case x: Double  => builder.field(key, x)
            case x: Float   => builder.field(key, x)
            case x: Boolean => builder.field(key, x)
            case x: Long    => builder.field(key, x)
            case x: Int     => builder.field(key, x)
            case x: Short   => builder.field(key, x)
            case x: Byte    => builder.field(key, x)
            case null       => builder.nullField(key)
          }
      }

    def addSeq(values: Seq[Any]): Unit =
      values.foreach {
        case map: Map[String, Any] =>
          builder.startObject()
          addMap(map)
          builder.endObject()
        case seq: Seq[Any] =>
          builder.startArray()
          addSeq(seq)
          builder.endArray()
        case product: Product =>
          builder.startArray()
          addSeq(product.productIterator.toSeq)
          builder.endArray()
        case other =>
          builder.autovalue(other)
      }

    addMap(source)
    builder
  }
}
