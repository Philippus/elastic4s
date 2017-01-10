package com.sksamuel.elastic4s.bulk

import java.util.concurrent.TimeUnit

import com.sksamuel.elastic4s.delete.{DeleteByIdDefinition, DeleteExecutables}
import com.sksamuel.elastic4s.index.IndexExecutables
import com.sksamuel.elastic4s.indexes.IndexDefinition
import org.elasticsearch.client.Client

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.control.NonFatal

class BulkProcessor(c: Client, processor: org.elasticsearch.action.bulk.BulkProcessor) {

  private val execs = new IndexExecutables with DeleteExecutables {}

  def add(index: IndexDefinition) = processor.add(execs.IndexDefinitionExecutable.builder(c, index).request())
  def add(delete: DeleteByIdDefinition) = processor.add(execs.DeleteByIdDefinitionExecutable.builder(c, delete).request())

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
