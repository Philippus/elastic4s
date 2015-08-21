package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticClient
import org.scalatest.Matchers
import org.scalatest.words.IncludeWord

trait ElasticMatchers extends Matchers {

  implicit class ElasticContain(word: IncludeWord)(implicit client: ElasticClient) {

  }
}
