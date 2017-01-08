package com.sksamuel.elastic4s.get

trait MultiGetApi extends GetDsl {
  def multiget(first: GetDefinition, rest: GetDefinition*): MultiGetDefinition = multiget(first +: rest)
  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = MultiGetDefinition(gets.toSeq)
}
