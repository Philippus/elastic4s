package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticClient

trait ClientProvider {
  def client: ElasticClient
}
