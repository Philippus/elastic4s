package com.sksamuel.elastic4s.ext

object Maps {

  import scala.collection.JavaConverters._

  /**
    * Given a nested map of strings, will create a flatted map, where the strings are joined with a separator.
    * So given Map("a" -> "b", "c" -> Map("d" -> "e")) then the output will be
    * Map("a" -> "b", "c.d" -> "e")
    */
  def flatten[V](map: Map[String, V], separator: String = "."): Map[String, V] = map.flatMap {
    case (key, value: Map[String, V]) => flatten(value).map { case (k, v) => s"$key$separator$k" -> v }
    case (key, value: java.util.Map[String, V]) => flatten(value.asScala.toMap[String, V]).map { case (k, v) => s"$key$separator$k" -> v }
    case (key, value: V) => Map(key -> value)
  }

  /**
    * Given a java.util.Map[k, AnyRef], this method will return a scala.collection.immutable.Map[k, AnyRef]
    * where each value has been transformed such that:
    *  - if the value is also a java map, then it will be recursively converted into a scala map
    *  - otherwise it will be returned as is
    */
  def deepAsScala[K](src: java.util.Map[K, AnyRef]): Map[K, AnyRef] = src.asScala.mapValues {
    case map: java.util.Map[K, AnyRef] => deepAsScala(map)
    case other => other
  }.toMap

  /**
    * Given a scala.collection.immutable.Map[k, AnyRef], this method will return a java.util.Map[k, AnyRef]
    * where each value has been transformed such that:
    *  - if the value is also a scala map, then it will be recursively converted into a java map
    *  - otherwise it will be returned as is
    */
  def deepAsJava[K](src: Map[K, AnyRef]): java.util.Map[K, AnyRef] = src.mapValues {
    case map: Map[K, AnyRef] => deepAsJava(map)
    case other => other
  }.toMap.asJava
}
