package com.sksamuel.elastic4s.bulk

import java.util.concurrent.TimeUnit

import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.indexes.IndexDefinition

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.control.NonFatal

class BulkProcessor(processor: org.elasticsearch.action.bulk.BulkProcessor) {

  def add(index: IndexDefinition) = processor.add(index.build)
  def add(delete: DeleteByIdDefinition) = processor.add(delete.build)

  def close(duration: FiniteDuration): Boolean = processor.awaitClose(duration.toNanos, TimeUnit.NANOSECONDS)

  def flush() = processor.flush()

  def close(): Future[Unit] = {
    val promise = Promise[Unit]
    new Thread(new Runnable {
      override def run(): Unit = {
        try {
          close(Integer.MAX_VALUE.days)
          promise.success(())
        } catch {
          case NonFatal(e) =>
            promise.failure(e)
        }
      }
    }).start()
    promise.future
  }
}