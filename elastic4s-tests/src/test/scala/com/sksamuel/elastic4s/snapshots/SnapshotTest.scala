package com.sksamuel.elastic4s.snapshots

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class SnapshotTest extends FlatSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  "createRepository" should "create a new repo" in {
    val resp = http.execute {
      createRepository("myrepo", "fs").settings(Map("location" -> "backup"))
    }.await
    resp.right.get.result.acknowledged shouldBe true
  }

  it should "error if no location set" in {
    http.execute {
      createRepository("myrepo", "fs")
    }.await.left.get.error.`type` shouldBe "repository_exception"
  }

  "createSnapshot" should "create a new snapshot" in {
    val resp = http.execute {
      createSnapshot("snap1", "myrepo")
    }.await
    resp.right.get.result.accepted shouldBe true
  }

  it should "error when the repo does not exist" in {
    http.execute {
      createSnapshot("snap1", "abbbbc")
    }.await.left.get.error.`type` shouldBe "repository_missing_exception"
  }

  "getSnapshot" should "return the named snapshot" in {
    val resp = http.execute {
      getSnapshot("snap1", "myrepo")
    }.await.right.get.result
    resp.snapshots.head.snapshot shouldBe "snap1"
    resp.snapshots.head.uuid should not be null
    resp.snapshots.head.version should not be null
  }

  it should "error when the snapshot does not exist" in {
    http.execute {
      getSnapshot("abc", "myrepo")
    }.await.left.get.error.`type` shouldBe "snapshot_missing_exception"
  }

  it should "error when the repo does not exist" in {
    http.execute {
      getSnapshot("snap1", "bbbbb")
    }.await.left.get.error.`type` shouldBe "repository_missing_exception"
  }

  "deleteSnapshot" should "remove the named snapshot" in {
    http.execute {
      deleteSnapshot("snap1", "myrepo")
    }.await.right.get.result.acknowledged shouldBe true
    http.execute {
      getSnapshot("snap1", "myrepo")
    }.await.left.get.error.`type` shouldBe "snapshot_missing_exception"
  }
}
