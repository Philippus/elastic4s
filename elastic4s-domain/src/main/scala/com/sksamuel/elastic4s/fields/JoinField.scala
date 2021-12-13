package com.sksamuel.elastic4s.fields

object JoinField {
  val `type`: String = "join"
}
case class JoinField(name: String,
                     eagerGlobalOrdinals: Option[Boolean] = None,
                     relations: Map[String, Any] = Map.empty,
                     meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = JoinField.`type`

  def relation(name: String, value: Any): JoinField = copy(relations = relations + (name -> value))
}
