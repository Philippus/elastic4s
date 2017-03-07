package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.embedded.LocalNode
import com.sksamuel.elastic4s.http.{HttpClient, HttpExecutable}
import com.sksamuel.elastic4s.{ElasticsearchClientUri, Executable, JsonFormat}
import org.elasticsearch.{ElasticsearchException, ElasticsearchWrapperException}
import org.scalatest.{Args, Suite, SuiteMixin}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait DualClient extends SuiteMixin with SharedDualElasticSugar {
  this: Suite =>

  private val logger = LoggerFactory.getLogger(getClass)

  var currentNode: LocalNode = getNode
  def node: LocalNode = currentNode

  // Runs twice (once for HTTP and once for TCP)
  protected def beforeRunTests(): Unit = {
  }

  var useHttpClient = true

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  def execute[T, R, Q1, Q2, C](request: T)(implicit tcpExec: Executable[T, R, Q1],
                                           httpExec: HttpExecutable[T, Q2],
                                           format: JsonFormat[Q2],
                                           tcpConv: CommonResponse[Q1, C],
                                           httpConv: CommonResponse[Q2, C]): Future[C] = {
    if (useHttpClient) {
      logger.debug("Using HTTP client...")
      httpExec.execute(http.rest, request, format).map(httpConv.convert)
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

  override abstract def runTests(testName: Option[String], args: Args) = {
    beforeRunTests()
    super.runTests(testName, args)

    // Get a new node for running the HTTP tests
    currentNode = getNode

    useHttpClient = !useHttpClient

    beforeRunTests()
    super.runTests(testName, args)
  }
}
