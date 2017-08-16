package com.sksamuel.elastic4s.http.values

case class Shards(total: Int,
                  failed: Int,
                  successful: Int)
