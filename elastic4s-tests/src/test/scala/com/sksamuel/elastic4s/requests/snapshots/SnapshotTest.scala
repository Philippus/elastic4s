package com.sksamuel.elastic4s.requests.snapshots

import java.util.UUID

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class SnapshotTest extends FlatSpec with Matchers with DockerTests {

  private val repoName = "repotest_" + UUID.randomUUID()

  "createRepository" should "create a new repo" in {
    val resp = client.execute {
      createRepository(repoName, "fs").settings(Map("location" -> ("/tmp/backup_" + UUID.randomUUID)))
    }.await
    resp.result.acknowledged shouldBe true
  }

  it should "error if no location set" in {
    client.execute {
      createRepository(repoName, "fs")
    }.await.error.`type` shouldBe "repository_exception"
  }

  "createSnapshot" should "create a new snapshot" in {
    val resp = client.execute {
      createSnapshot("snap1", repoName)
    }.await
    resp.result.accepted shouldBe true
  }

  it should "error when the repo does not exist" in {
    client.execute {
      createSnapshot("snap1", "abbbbc")
    }.await.error.`type` shouldBe "repository_missing_exception"
  }

  "getSnapshot" should "return the named snapshot" in {
    val resp = client.execute {
      getSnapshot("snap1", repoName)
    }.await.result
    resp.snapshots.head.snapshot shouldBe "snap1"
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
      getSnapshot("snap1", "bbbbb")
    }.await.error.`type` shouldBe "repository_missing_exception"
  }

  "deleteSnapshot" should "remove the named snapshot" in {
    client.execute {
      deleteSnapshot("snap1", repoName)
    }.await.result.acknowledged shouldBe true
    client.execute {
      getSnapshot("snap1", repoName)
    }.await.error.`type` shouldBe "snapshot_missing_exception"
  }
}
