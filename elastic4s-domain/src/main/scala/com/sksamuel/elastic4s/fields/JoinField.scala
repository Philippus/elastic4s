package com.sksamuel.elastic4s.fields

case class JoinField(name: String,
                     eagerGlobalOrdinals: Option[Boolean] = None,
                     relations: Map[String, Any] = Map.empty,
                     meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "join"
  def relation(name: String, value: Any): JoinField = copy(relations = relations + (name -> value))
}
