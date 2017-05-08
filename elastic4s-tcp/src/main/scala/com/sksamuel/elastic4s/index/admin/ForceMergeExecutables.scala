package com.sksamuel.elastic4s.index.admin

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.indexes.admin.ForceMergeDefinition
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ForceMergeExecutables {

  implicit object ForceMergeExecutable
    extends Executable[ForceMergeDefinition, ForceMergeResponse, ForceMergeResponse] {
    override def apply(c: Client, t: ForceMergeDefinition): Future[ForceMergeResponse] = {
      val builder = c.admin.indices.prepareForceMerge(t.indexes: _*)
      t.flush.foreach(builder.setFlush)
      t.onlyExpungeDeletes.foreach(builder.setOnlyExpungeDeletes)
      t.maxSegments.foreach(builder.setMaxNumSegments)
      injectFuture(builder.execute)
    }
  }

}
