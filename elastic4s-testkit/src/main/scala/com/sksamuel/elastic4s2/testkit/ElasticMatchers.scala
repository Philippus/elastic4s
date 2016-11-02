package com.sksamuel.elastic4s2.testkit

trait ElasticMatchers extends SearchMatchers with IndexMatchers

object ElasticMatchers extends ElasticMatchers
