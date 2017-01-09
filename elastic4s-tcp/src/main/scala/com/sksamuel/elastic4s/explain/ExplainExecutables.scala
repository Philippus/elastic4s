package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.explain.ExplainResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ExplainExecutables {
  implicit object ExplainDefinitionExecutable extends Executable[ExplainDefinition, ExplainResponse, ExplainResponse] {
    override def apply(c: Client, t: ExplainDefinition): Future[ExplainResponse] = {
      val builder = t.build(c.prepareExplain(t.index, t.`type`, t.id))
      injectFuture(builder.execute)
    }
  }
}
