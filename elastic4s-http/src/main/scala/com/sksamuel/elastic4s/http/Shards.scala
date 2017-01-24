package com.sksamuel.elastic4s.http

case class Shards(total: Int,
                  failed: Int,
                  successful: Int)
