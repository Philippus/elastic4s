package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.index.admin.RefreshIndexResponse
import com.sksamuel.elastic4s.{IndexAndTypes, Indexes}
import org.elasticsearch.ResourceAlreadyExistsException
import org.elasticsearch.transport.RemoteTransportException
import org.scalatest.Suite

/**
  * Provides helper methods for things like refreshing an index, and blocking until an
  * index has a certain count of documents. These methods are very useful when writing
  * tests to allow for blocking, iterative coding
  */
trait HttpElasticSugar extends ElasticDsl {
  this: Suite with LocalNodeProvider =>

  // refresh all indexes
  def refreshAll(): RefreshIndexResponse = refresh(Indexes.All)

  // refreshes all specified indexes
  def refresh(indexes: Indexes): RefreshIndexResponse =
    http
      .execute {
        refreshIndex(indexes)
      }
      .await
      .right
      .get
      .result

  def blockUntilGreen(): Unit =
    blockUntil("Expected cluster to have green status") { () =>
      http
        .execute {
          clusterHealth()
        }
        .await
        .right
        .get
        .result
        .status
        .toUpperCase == "GREEN"
    }

  def blockUntil(explain: String)(predicate: () => Boolean): Unit = {

    var backoff = 0
    var done    = false

    while (backoff <= 16 && !done) {
      if (backoff > 0) Thread.sleep(200 * backoff)
      backoff = backoff + 1
      try {
        done = predicate()
      } catch {
        case e: Throwable =>
          logger.warn("problem while testing predicate", e)
      }
    }

    require(done, s"Failed waiting on: $explain")
  }

  def ensureIndexExists(index: String): Unit =
    try {
      http.execute {
        createIndex(index)
      }.await
    } catch {
      case _: ResourceAlreadyExistsException => // Ok, ignore.
      case _: RemoteTransportException       => // Ok, ignore.
    }

  def doesIndexExists(name: String): Boolean =
    http
      .execute {
        indexExists(name)
      }
      .await
      .right
      .get
      .result
      .isExists

  def deleteIndex(name: String): Unit =
    if (doesIndexExists(name)) {
      http.execute {
        ElasticDsl.deleteIndex(name)
      }.await
    }

  def truncateIndex(index: String): Unit = {
    deleteIndex(index)
    ensureIndexExists(index)
    blockUntilEmpty(index)
  }

  def blockUntilDocumentExists(id: String, index: String): Unit =
    blockUntil(s"Expected to find document $id") { () =>
      val resp = http
        .execute {
          get(id).from(index)
        }
        .await
        .right
        .get
      resp.isSuccess && resp.result.exists
    }

  @deprecated("Use blockUntilDocumentExists(id, string) because types will be removed in elasticsearch 7.0", "6.0")
  def blockUntilDocumentExists(id: String, index: String, `type`: String): Unit =
    blockUntil(s"Expected to find document $id") { () =>
      val resp = http
        .execute {
          get(id).from(index / `type`)
        }
        .await
        .right
        .get
      resp.isSuccess && resp.result.exists
    }

  def blockUntilCount(expected: Long, index: String): Unit =
    blockUntil(s"Expected count of $expected") { () =>
      val result = http
        .execute {
          search(index).matchAllQuery().size(0)
        }
        .await
        .right
        .get
      expected <= result.result.totalHits
    }

  @deprecated
  def blockUntilCount(expected: Long, indexAndTypes: IndexAndTypes): Unit =
    blockUntil(s"Expected count of $expected") { () =>
      val result = http
        .execute {
          search(indexAndTypes).matchAllQuery().size(0)
        }
        .await
        .right
        .get
      expected <= result.result.totalHits
    }

  /**
    * Will block until the given index and optional types have at least the given number of documents.
    */
  def blockUntilCount(expected: Long, index: String, types: String*): Unit =
    blockUntil(s"Expected count of $expected") { () =>
      val result = http
        .execute {
          search(index / types).matchAllQuery().size(0)
        }
        .await
        .right
        .get
      expected <= result.result.totalHits
    }

  def blockUntilExactCount(expected: Long, index: String, types: String*): Unit =
    blockUntil(s"Expected count of $expected") { () =>
      expected == http
        .execute {
          search(index / types).size(0)
        }
        .await
        .right
        .get
        .result
        .totalHits
    }

  def blockUntilEmpty(index: String): Unit =
    blockUntil(s"Expected empty index $index") { () =>
      http
        .execute {
          search(Indexes(index)).size(0)
        }
        .await
        .right
        .get
        .result
        .totalHits == 0
    }

  def blockUntilIndexExists(index: String): Unit =
    blockUntil(s"Expected exists index $index") { () ⇒
      doesIndexExists(index)
    }

  def blockUntilIndexNotExists(index: String): Unit =
    blockUntil(s"Expected not exists index $index") { () ⇒
      !doesIndexExists(index)
    }

  def blockUntilDocumentHasVersion(index: String, `type`: String, id: String, version: Long): Unit =
    blockUntil(s"Expected document $id to have version $version") { () =>
      val resp = http
        .execute {
          get(id).from(index / `type`)
        }
        .await
        .right
        .get
      resp.isSuccess && resp.result.version == version
    }
}
