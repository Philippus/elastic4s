package com.sksamuel.elastic4s.requests.reloadsearchanalyzers

import com.sksamuel.elastic4s.Indexes

case class ReloadSearchAnalyzersRequest(indexes: Indexes) extends Serializable
