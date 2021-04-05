package com.sksamuel.elastic4s.requests.indexes

case class IndexMappings(index: String, mappings: Map[String, Any], meta: Map[String, Any] = Map.empty[String, Any])

case class IndexFieldMapping(index: String, fieldMappings: Seq[FieldMapping])
case class FieldMapping(fullName:String, mappings: Map[String, Any])

case class PutMappingResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}
