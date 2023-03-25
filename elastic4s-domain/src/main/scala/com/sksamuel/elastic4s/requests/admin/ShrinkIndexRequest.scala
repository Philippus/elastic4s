package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class ShrinkIndexRequest(source: String,
                              target: String,
                              waitForActiveShards: Option[Int] = None,
                              settings: Map[String, String] = Map.empty,
                              shards: Option[Int] = None) {

  def shards(shards: Int): ShrinkIndexRequest = copy(shards = shards.some)
  def settings(map: Map[String, String]): ShrinkIndexRequest = copy(settings = map)
}
