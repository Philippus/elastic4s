package com.sksamuel.elastic4s.requests.snapshots

import java.util.UUID
import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SnapshotTest extends AnyFlatSpec with Matchers with DockerTests {

  private val repoName     = "repotest_" + UUID.randomUUID().toString
  private val snapshotName = "snap1"
  private val settings     = Map("location" -> ("/tmp/elastic4s/backup_" + UUID.randomUUID.toString))

  "createRepository" should "create a new repo" in {
    val resp = client.execute {
      createRepository(repoName, "fs").settings(settings)
    }.await
    resp.result.acknowledged shouldBe true
  }

  it should "error if no location set" in {
    client.execute {
      createRepository(repoName, "fs")
    }.await.error.`type` shouldBe "repository_exception"
  }

  "getRepository" should "return the named snapshot repository" in {
    val resp = client.execute {
      getRepository(repoName)
    }.await.result

    resp.repositories.head._1 shouldBe repoName
    resp.repositories.head._2.`type` shouldBe "fs"
    resp.repositories.head._2.settings shouldBe settings
  }

  it should "error when the repo does not exist" in {
    client.execute {
      getRepository("not_exists")
    }.await.error.`type` shouldBe "repository_missing_exception"
  }

  "deleteRepository" should "delete a snapshot repository" in {
    val deleteRepoName = "delete_repotest_" + UUID.randomUUID().toString
    client.execute {
      createRepository(deleteRepoName, "fs").settings(
        Map("location" -> ("/tmp/elastic4s/backup_" + UUID.randomUUID.toString))
      )
    }.await

    client.execute {
      deleteRepository(deleteRepoName)
    }.await.result.acknowledged shouldBe true

    client.execute {
      getRepository(deleteRepoName)
    }.await.error.`type` shouldBe "repository_missing_exception"
  }

  it should "error when the repo does not exist" in {
    client.execute {
      getRepository("not_exists")
    }.await.error.`type` shouldBe "repository_missing_exception"
  }

  "createSnapshot" should "create a new snapshot" in {
    // ensure there's at least one index to restore, so that we can test some of the behaviour around that
    createIdx("tmpidx").isSuccess shouldBe true
    client.execute {
      createSnapshot("snap0", repoName)
    }.await.result.succeeded shouldBe true
  }

  it should "deserialize when waitForCompletion = true" in {
    client.execute {
      createSnapshot(snapshotName, repoName) waitForCompletion true
    }.await.result.succeeded shouldBe true
  }

  it should "error when the repo does not exist" in {
    client.execute {
      createSnapshot(snapshotName, "abbbbc")
    }.await.error.`type` shouldBe "repository_missing_exception"
  }

  "getSnapshot" should "return the named snapshot" in {
    val resp = client.execute {
      getSnapshot(snapshotName, repoName)
    }.await.result
    resp.snapshots.head.snapshot shouldBe snapshotName
    resp.snapshots.head.uuid should not be null
    resp.snapshots.head.version should not be null
  }

  it should "error when the snapshot does not exist" in {
    client.execute {
      getSnapshot("abc", repoName)
    }.await.error.`type` shouldBe "snapshot_missing_exception"
  }

  it should "error when the repo does not exist" in {
    client.execute {
      getSnapshot(snapshotName, "bbbbb")
    }.await.error.`type` shouldBe "repository_missing_exception"
  }

  "restore snapshot" should "error when an index clashes" in {
    client.execute {
      restoreSnapshot(snapshotName, repoName) waitForCompletion true
    }.await.error.`type` shouldBe "snapshot_restore_exception"
  }

  it should "succeed when request is correct" in {
    client.execute(deleteIndex("tmpidx")).await.isSuccess shouldBe true
    client.execute {
      restoreSnapshot(snapshotName, repoName) index Index("tmpidx")
    }.await.result.succeeded shouldEqual true
  }

  it should "succeeded if waitForCompletion = false" in {
    client.execute(deleteIndex("tmpidx")).await.isSuccess shouldBe true
    client.execute {
      restoreSnapshot(snapshotName, repoName) index Index("tmpidx") waitForCompletion false
    }.await.result.succeeded shouldEqual true
  }

  it should "succeeded if waitForCompletion = true" in {
    client.execute(deleteIndex("tmpidx")).await.isSuccess shouldBe true
    client.execute {
      restoreSnapshot(snapshotName, repoName) index Index("tmpidx") waitForCompletion true
    }.await.result.succeeded shouldEqual true
  }

  it should "error when the snapshot does not exist" in {
    client.execute {
      restoreSnapshot("missing_snapshot", repoName)
    }.await.error.`type` shouldBe "snapshot_restore_exception"
  }

  it should "error when the repo does not exist" in {
    client.execute {
      restoreSnapshot(snapshotName, "missing_repo")
    }.await.error.`type` shouldBe "repository_missing_exception"
  }

  "deleteSnapshot" should "remove the named snapshot" in {
    client.execute {
      deleteSnapshot(snapshotName, repoName)
    }.await.result.acknowledged shouldBe true
    client.execute {
      getSnapshot(snapshotName, repoName)
    }.await.error.`type` shouldBe "snapshot_missing_exception"
  }
}
