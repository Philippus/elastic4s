package com.sksamuel.elastic4s.requests.searches.queries

trait Query

object NoopQuery extends Query

trait MultiTermQuery extends Query
