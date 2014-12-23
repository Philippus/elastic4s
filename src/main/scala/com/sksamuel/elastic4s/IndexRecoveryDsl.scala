package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.recovery.RecoveryRequest

trait IndexRecoveryDsl {

  class IndexRecoveryDefinition(indices: String*) {
    private def builder = new RecoveryRequest(indices: _*)
    def build = builder
  }
}
