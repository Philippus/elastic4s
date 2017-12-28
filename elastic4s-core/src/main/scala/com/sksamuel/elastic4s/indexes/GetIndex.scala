package com.sksamuel.elastic4s.indexes

@deprecated("this class is now called GetIndex", "6.1.2")
class GetIndexDefinition(index: String) extends GetIndex(index)

case class GetIndex(index: String)
