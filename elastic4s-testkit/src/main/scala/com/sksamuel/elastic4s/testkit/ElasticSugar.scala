package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.requests.indexes.admin.RefreshIndexResponse
import com.sksamuel.elastic4s.{ElasticDsl, IndexAndTypes, Indexes}
import org.scalatest.Suite

import scala.util.Try

/**
  * Provides helper methods for things like refreshing an index, and blocking until an
  * index has a certain count of documents. These methods are very useful when writing
  * tests to allow for blocking, iterative coding
  */
trait ElasticSugar extends ElasticDsl {
  this: Suite with ClientProvider =>

  // refresh all indexes
  def refreshAll(): RefreshIndexResponse = refresh(Indexes.All)

  // refreshes all specified indexes
  def refresh(indexes: Indexes): RefreshIndexResponse =
    client
      .execute {
        refreshIndex(indexes)
      }
      .await
      .result

  def blockUntilGreen(): Unit =
    blockUntil("Expected cluster to have green status") { () =>
      client
        .execute {
          clusterHealth()
        }
        .await
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
      try done = predicate()
      catch {
        case e: Throwable =>
          logger.warn("problem while testing predicate", e)
      }
    }

    require(done, s"Failed waiting on: $explain")
  }

  def ensureIndexExists(index: String): Unit =
    if (!doesIndexExists(index))
      client.execute {
        createIndex(index)
      }.await

  def doesIndexExists(name: String): Boolean =
    client
      .execute {
        indexExists(name)
      }
      .await
      .result
      .isExists

  def deleteIndex(name: String): Unit =
    Try {
      client.execute {
        ElasticDsl.deleteIndex(name)
      }.await
    }

  def truncateIndex(index: String): Unit = {
    deleteIndex(index)
    ensureIndexExists(index)
    blockUntilEmpty(index)
  }

  def blockUntilDocumentExists(id: String, index: String, `type`: String): Unit =
    blockUntil(s"Expected to find document $id") { () =>
      client
        .execute {
          get(id).from(index / `type`)
        }
        .await
        .result
        .exists
    }

  def blockUntilCount(expected: Long, index: String): Unit =
    blockUntil(s"Expected count of $expected") { () =>
      val result = client
        .execute {
          search(index).matchAllQuery().size(0)
        }
        .await
        .result
      expected <= result.totalHits
    }

  def blockUntilCount(expected: Long, indexAndTypes: IndexAndTypes): Unit =
    blockUntil(s"Expected count of $expected") { () =>
      val result = client
        .execute {
          search(indexAndTypes).matchAllQuery().size(0)
        }
        .await
        .result
      expected <= result.totalHits
    }

  /**
    * Will block until the given index and optional types have at least the given number of documents.
    */
  def blockUntilCount(expected: Long, index: String, types: String*): Unit =
    blockUntil(s"Expected count of $expected") { () =>
      val result = client
        .execute {
          search(index / types).matchAllQuery().size(0)
        }
        .await
        .result
      expected <= result.totalHits
    }

  def blockUntilExactCount(expected: Long, index: String, types: String*): Unit =
    blockUntil(s"Expected count of $expected") { () =>
      expected == client
        .execute {
          search(index / types).size(0)
        }
        .await
        .result
        .totalHits
    }

  def blockUntilEmpty(index: String): Unit =
    blockUntil(s"Expected empty index $index") { () =>
      client
        .execute {
          search(Indexes(index)).size(0)
        }
        .await
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
      client
        .execute {
          get(id).from(index / `type`)
        }
        .await
        .result
        .version == version
    }
}
