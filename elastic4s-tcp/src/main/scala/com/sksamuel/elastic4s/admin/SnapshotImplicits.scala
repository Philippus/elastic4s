package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.snapshots.{
  CreateRepository,
  CreateSnapshot,
  DeleteSnapshot,
  GetSnapshots,
  RestoreSnapshot
}
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse
import org.elasticsearch.client.Client

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait SnapshotImplicits {

  implicit object DeleteSnapshotDefinitionExecutable
      extends Executable[DeleteSnapshot, DeleteSnapshotResponse, DeleteSnapshotResponse] {
    override def apply(c: Client, t: DeleteSnapshot): Future[DeleteSnapshotResponse] = {
      val builder = c.admin().cluster().prepareDeleteSnapshot(t.repositoryName, t.snapshotName)
      injectFuture(builder.execute(_))
    }
  }

  implicit object RestoreSnapshotDefinitionExecutable
      extends Executable[RestoreSnapshot, RestoreSnapshotResponse, RestoreSnapshotResponse] {
    override def apply(c: Client, t: RestoreSnapshot): Future[RestoreSnapshotResponse] = {
      val builder = c.admin().cluster().prepareRestoreSnapshot(t.repositoryName, t.snapshotName)
      injectFuture(builder.execute(_))
    }
  }

  implicit object CreateSnapshotDefinitionExecutable
      extends Executable[CreateSnapshot, CreateSnapshotResponse, CreateSnapshotResponse] {
    override def apply(c: Client, t: CreateSnapshot): Future[CreateSnapshotResponse] = {
      val builder = c.admin().cluster().prepareCreateSnapshot(t.repositoryName, t.snapshotName)
      if (!t.indices.isAll)
        builder.setIndices(t.indices.values: _*)
      t.includeGlobalState.foreach(builder.setIncludeGlobalState)
      t.waitForCompletion.foreach(builder.setWaitForCompletion)
      t.includeGlobalState.foreach(builder.setPartial)
      t.includeGlobalState.foreach(builder.setIncludeGlobalState)
      injectFuture(builder.execute(_))
    }
  }

  implicit object GetSnapshotsDefinitionExecutable
      extends Executable[GetSnapshots, GetSnapshotsResponse, GetSnapshotsResponse] {
    override def apply(c: Client, t: GetSnapshots): Future[GetSnapshotsResponse] = {
      val builder = c.admin().cluster().prepareGetSnapshots(t.repositoryName)
      builder.setSnapshots(t.snapshotNames: _*)
      t.ignoreUnavailable.foreach(builder.setIgnoreUnavailable)
      t.verbose.foreach(builder.setVerbose)
      injectFuture(builder.execute(_))
    }
  }

  implicit object CreateRepositoryDefinitionExecutable
      extends Executable[CreateRepository, PutRepositoryResponse, PutRepositoryResponse] {
    override def apply(c: Client, t: CreateRepository): Future[PutRepositoryResponse] = {
      val builder = c.admin().cluster().preparePutRepository(t.name)
      builder.setType(t.`type`)
      if (t.settings.nonEmpty)
        builder.setSettings(t.settings.asJava)
      t.verify.foreach(builder.setVerify)
      injectFuture(builder.execute(_))
    }
  }
}
