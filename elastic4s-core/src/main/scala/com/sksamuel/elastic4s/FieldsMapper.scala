package com.sksamuel.elastic4s

/**
 * Converts between scala types and types that Elasticsearch understands.
 */
object FieldsMapper {

  import scala.collection.JavaConverters._

  def mapper(m: Map[String, Any]): Map[String, AnyRef] = {
    m map {
      case null => null
      case (name: String, nest: Map[_, _]) => name -> mapper(nest.asInstanceOf[Map[String, Any]]).asJava
      case (name: String, iter: Iterable[_]) => name -> iter.map(mapper).toArray
      case (name: String, a: AnyRef) => name -> a
      case (name: String, a: Any) => name -> a.toString
    }
  }

  def mapper(a: Any): AnyRef = {
    a match {
      case map: Map[_, _] => map.map { case (key, value) => key -> mapper(value) }.asJava
      case iter: Iterable[_] => iter.map(mapper).toArray
      case a: AnyRef => a
      case a: Any => a.toString
      case null => null
    }
  }

  def mapFields(fields: Map[String, Any]): Seq[FieldValue] = {
    fields map {
      case (name: String, nest: Map[_, _]) =>
        val nestedFields = mapFields(nest.asInstanceOf[Map[String, Any]])
        NestedFieldValue(Some(name), nestedFields)

      case (name: String, nest: Array[Map[_, _]]) =>
        val nested = nest.map(n => new NestedFieldValue(None, mapFields(n.asInstanceOf[Map[String, Any]])))
        ArrayFieldValue(name, nested)

      case (name: String, arr: Array[Any]) =>
        val values = arr.map(new SimpleFieldValue(None, _))
        ArrayFieldValue(name, values)

      case (name: String, a: FieldValue) =>
        NestedFieldValue(name, Seq(a))

      case (name: String, s: Iterable[_]) =>
        s.headOption match {
          case Some(m: Map[_, _]) =>
            val nested = s.map(n => new NestedFieldValue(None, mapFields(n.asInstanceOf[Map[String, Any]])))
            ArrayFieldValue(name, nested.toSeq)

          case Some(a: Any) =>
            val values = s.map(new SimpleFieldValue(None, _))
            ArrayFieldValue(name, values.toSeq)

          case _ =>
            // can't work out or empty - map to empty
            ArrayFieldValue(name, Seq.empty)
        }

      case (name: String, a: Any) =>
        SimpleFieldValue(Some(name), a)

      case (name: String, _) =>
        NullFieldValue(name)
    }
  }.toSeq
}
