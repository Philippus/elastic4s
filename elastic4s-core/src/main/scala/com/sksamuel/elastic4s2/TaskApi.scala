package com.sksamuel.elastic4s2

import org.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksResponse
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksResponse
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait TaskApi {

  implicit object ListTasksDefinitionExecutable
    extends Executable[ListTasksDefinition, ListTasksResponse, ListTasksResponse] {
    override def apply(client: Client, d: ListTasksDefinition): Future[ListTasksResponse] = {
      val builder = client.admin().cluster().prepareListTasks(d.nodeIds: _*)
      d.waitForCompletion.foreach(builder.setWaitForCompletion)
      d.detailed.foreach(builder.setDetailed)
      injectFuture(builder.execute)
    }
  }

  implicit object CancelTasksDefinitionExecutable
    extends Executable[CancelTasksDefinition, CancelTasksResponse, CancelTasksResponse] {
    override def apply(client: Client, d: CancelTasksDefinition): Future[CancelTasksResponse] = {
      val builder = client.admin().cluster().prepareCancelTasks(d.nodeIds: _*)
      d.timeout.foreach(duration => builder.setTimeout(TimeValue.timeValueNanos(duration.toNanos)))
      builder.setActions(d.actions: _*)
      injectFuture(builder.execute)
    }
  }

  implicit object PendingClusterTasksDefinitionExecutable
    extends Executable[PendingClusterTasksDefinition, PendingClusterTasksResponse, PendingClusterTasksResponse] {
    override def apply(client: Client, d: PendingClusterTasksDefinition): Future[PendingClusterTasksResponse] = {
      val builder = client.admin().cluster().preparePendingClusterTasks()
      builder.setLocal(d.local)
      d.masterNodeTimeout.foreach(duration => builder.setMasterNodeTimeout(TimeValue.timeValueNanos(duration.toNanos)))
      injectFuture(builder.execute)
    }
  }
}

case class CancelTasksDefinition(nodeIds: Seq[String],
                                 timeout: Option[FiniteDuration] = None,
                                 actions: Seq[String] = Nil)

case class PendingClusterTasksDefinition(local: Boolean,
                                         masterNodeTimeout: Option[FiniteDuration] = None)

case class ListTasksDefinition(nodeIds: Seq[String],
                               detailed: Option[Boolean] = None,
                               waitForCompletion: Option[Boolean] = None)
