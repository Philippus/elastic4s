package com.sksamuel.elastic4s.handlers.cluster

import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpResponse, ResponseHandler}
import com.sksamuel.elastic4s.ext.OptionImplicits.{RichOption, RichOptionImplicits}

import scala.concurrent.duration.Duration

object NodeHotThreadsHandler extends Handler[NodeHotThreadsRequest, String] {
  override def build(t: NodeHotThreadsRequest): ElasticRequest = {

    val endpoint = t.nodeId match {
      case Some(nodeId) => s"/_nodes/$nodeId/hot_threads"
      case _            => "/_nodes/hot_threads"
    }

    val params = scala.collection.mutable.Map.empty[String, String]
    t.ignoreIdleThreads.map(_.toString).foreach(params.put("ignore_idle_threads", _))
    t.interval.foreach(params.put("interval", _))
    t.snapshots.map(_.toString).foreach(params.put("snapshots", _))
    t.threads.map(_.toString).foreach(params.put("threads", _))
    t.masterTimeout.foreach(params.put("master_timeout", _))
    t.timeout.foreach(params.put("timeout", _))
    t.`type`.foreach(params.put("type", _))

    ElasticRequest("GET", endpoint, params.toMap)
  }

  override def responseHandler: ResponseHandler[String] = new ResponseHandler[String] {

    override def handle(response: HttpResponse): Either[ElasticError, String] = response.statusCode match {
      case 200 | 201 | 202 | 203 | 204 =>
        val entity = response.entity.getOrError("No entity defined")
        Right(entity.content)
      case _                           =>
        Left(ElasticErrorParser.parse(response))
    }
  }
}

case class NodeHotThreadsRequest(
    nodeId: Option[String] = None,
    // If true, known idle threads (e.g. waiting in a socket select, or to get a task from an empty queue) are filtered out
    ignoreIdleThreads: Option[Boolean] = None,
    //   The interval to do the second sampling of threads
    interval: Option[String] = None,
    // Number of samples of thread stacktrace
    snapshots: Option[Int] = None,
    // Specifies the number of hot threads to provide information for
    threads: Option[Int] = None,
    // The type to sample. Available options are block, cpu, and wait
    `type`: Option[String] = None,
    // Specifies the period of time to wait for a connection to the master node
    masterTimeout: Option[String] = None,
    // Specifies the period of time to wait for a response
    timeout: Option[String] = None
) {

  def snapshots(snapshots: Int): NodeHotThreadsRequest          = copy(snapshots = snapshots.some)
  def threads(threads: Int): NodeHotThreadsRequest              = copy(threads = threads.some)
  def ignoreIdleThreads(ignore: Boolean): NodeHotThreadsRequest = copy(ignoreIdleThreads = ignore.some)
  def `type`(t: String): NodeHotThreadsRequest                  = copy(`type` = t.some)

  def masterTimeout(timeout: Duration): NodeHotThreadsRequest = copy(masterTimeout = s"${timeout.toNanos}nanos".some)
  def masterTimeout(timeout: String): NodeHotThreadsRequest   = copy(masterTimeout = timeout.some)

  def timeout(timeout: Duration): NodeHotThreadsRequest = copy(timeout = s"${timeout.toNanos}nanos".some)
  def timeout(timeout: String): NodeHotThreadsRequest   = copy(timeout = timeout.some)

  def interval(interval: Duration): NodeHotThreadsRequest = copy(interval = s"${interval.toNanos}nanos".some)
  def interval(interval: String): NodeHotThreadsRequest   = copy(interval = interval.some)
}
