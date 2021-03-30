package com.sksamuel.elastic4s.requests.admin

case class ShrinkIndexRequest(source: String,
                              target: String,
                              waitForActiveShards: Option[Int] = None,
                              settings: Map[String, String] = Map.empty) {

  def settings(map: Map[String, String]): ShrinkIndexRequest = copy(settings = map)
}
