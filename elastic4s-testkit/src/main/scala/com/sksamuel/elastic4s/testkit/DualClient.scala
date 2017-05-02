package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.embedded.LocalNode
import com.sksamuel.elastic4s.http.{HttpClient, HttpExecutable}
import com.sksamuel.elastic4s.{ElasticsearchClientUri, Executable, TcpClient}
import org.elasticsearch.{ElasticsearchException, ElasticsearchWrapperException}
import org.scalatest._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait DualClient extends SuiteMixin {
  this: Suite with DualElasticSugar =>

  var node: LocalNode = getNode
  var client: TcpClient = node.elastic4sclient(false)

  private val logger = LoggerFactory.getLogger(getClass)

  // Runs twice (once for HTTP and once for TCP)
  protected def beforeRunTests(): Unit = {
  }

  var useHttpClient = true

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  def execute[T, R, Q1, Q2](request: T)(implicit tcpExec: Executable[T, R, Q1],
                                        httpExec: HttpExecutable[T, Q2],
                                        tcpConv: ResponseConverter[Q1, Q2]): Future[Q2] = {
    if (useHttpClient) {
      logger.debug("Using HTTP client...")
      httpExec.execute(http.rest, request)
    } else {
      try {
        logger.debug("Using TCP client...")
        tcpExec(client.java, request).map(tcpConv.convert)
      } catch {
        case e: ElasticsearchException => Future.failed(e)
        case e: ElasticsearchWrapperException => Future.failed(e)
      }
    }
  }

  override abstract def runTests(testName: Option[String], args: Args): Status = {
    val httpStatus = runTestsOnce(testName, args)

    // Get a new node for running the TCP tests
    node = getNode
    client = node.elastic4sclient(false)
    useHttpClient = !useHttpClient

    val tcpStatus = runTestsOnce(testName, args)

    new CompositeStatus(Set(httpStatus, tcpStatus))
  }

  private def runTestsOnce(testName: Option[String], args: Args): Status = {
    try {
      beforeRunTests()
      super.runTests(testName, args)
    } finally {
      node.stop(true)
    }
  }

  def tcpOnly(block: => Unit): Unit = if (!useHttpClient) block
  def httpOnly(block: => Unit): Unit = if (useHttpClient) block
}
