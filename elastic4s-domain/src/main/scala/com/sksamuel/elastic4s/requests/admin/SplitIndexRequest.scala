package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class SplitIndexRequest(
    source: String,
    target: String,
    waitForActiveShards: Option[Int] = None,
    shards: Option[Int] = None,
    settings: Map[String, String] = Map.empty
) {

  def shards(shards: Int): SplitIndexRequest                = copy(shards = shards.some)
  def settings(map: Map[String, String]): SplitIndexRequest = copy(settings = map)
}
