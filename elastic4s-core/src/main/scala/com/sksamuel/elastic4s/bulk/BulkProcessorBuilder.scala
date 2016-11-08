package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.bulk.BulkProcessor.Listener
import org.elasticsearch.action.bulk.{BackoffPolicy, BulkRequest, BulkResponse}
import org.elasticsearch.common.unit.{ByteSizeUnit, ByteSizeValue, TimeValue}

import scala.concurrent.duration.FiniteDuration

case class BulkProcessorBuilder(name: Option[String] = None,
                                count: Option[Int] = None,
                                backoffPolicy: Option[BackoffPolicy] = None,
                                concurrentRequests: Option[Int] = None,
                                flushInterval: Option[FiniteDuration] = None,
                                size: Option[ByteSizeValue] = None) {

  def build(client: ElasticClient): BulkProcessor = {
    val builder = org.elasticsearch.action.bulk.BulkProcessor.builder(client.java, new Listener {

      override def beforeBulk(executionId: Long, request: BulkRequest): Unit = ()

      override def afterBulk(executionId: Long,
                             request: BulkRequest,
                             response: BulkResponse): Unit = ()

      override def afterBulk(executionId: Long,
                             request: BulkRequest,
                             failure: Throwable): Unit = ()
    })

    backoffPolicy.foreach(builder.setBackoffPolicy)
    concurrentRequests.foreach(builder.setConcurrentRequests)
    count.foreach(builder.setBulkActions)
    flushInterval.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setFlushInterval)
    name.foreach(builder.setName)
    size.foreach(builder.setBulkSize)

    new BulkProcessor(builder.build())
  }

  def name(name: String): BulkProcessorBuilder = copy(name = name.some)
  def backoffPolicy(backoffPolicy: BackoffPolicy): BulkProcessorBuilder = copy(backoffPolicy = backoffPolicy.some)

  def concurrentRequests(concurrentRequests: Int): BulkProcessorBuilder =
    copy(concurrentRequests = concurrentRequests.some)

  def flushInterval(flushInterval: FiniteDuration): BulkProcessorBuilder = copy(flushInterval = flushInterval.some)
  def actionCount(count: Int): BulkProcessorBuilder = copy(count = count.some)

  def actionSize(units: Int, unit: ByteSizeUnit): BulkProcessorBuilder =
    copy(size = new ByteSizeValue(units, unit).some)
}


