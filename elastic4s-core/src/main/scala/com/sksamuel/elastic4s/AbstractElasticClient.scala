package com.sksamuel.elastic4s

import org.elasticsearch.client.Client

trait AbstractElasticClient {
  def close(): Unit
  // return the underlying Java TCP client
  def java: Client
}
