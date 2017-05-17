package com.sksamuel.elastic4s.admin

case class ShrinkDefinition(source: String,
                            target: String,
                            waitForActiveShards: Option[Int] = None,
                            settings: Map[String, String] = Map.empty) {

  def settings(map: Map[String, String]): ShrinkDefinition = copy(settings = settings)
}
