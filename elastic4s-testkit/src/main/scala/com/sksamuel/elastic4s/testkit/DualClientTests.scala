package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.{ElasticApi, Executable}
import com.sksamuel.exts.Logging
import org.elasticsearch.{ElasticsearchException, ElasticsearchWrapperException}
import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait DualClientTests
  extends SuiteMixin
    with DiscoveryLocalNodeProvider
    with ElasticApi
    with Logging
    with com.sksamuel.elastic4s.ElasticDsl
    with com.sksamuel.elastic4s.http.ElasticDsl {
  this: Suite =>

  var useHttpClient = true
  private def nextIndexName = "index" + Random.nextInt
  protected var indexname: String = nextIndexName

  // runs twice (once for HTTP and once for TCP)
  protected def beforeRunTests(): Unit = {
  }

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
    useHttpClient = !useHttpClient
    val tcpStatus = runTestsOnce(testName, args)
    new CompositeStatus(Set(httpStatus, tcpStatus))
  }

  private def runTestsOnce(testName: Option[String], args: Args): Status = {
    indexname = nextIndexName
    beforeRunTests()
    super.runTests(testName, args)
  }

  def tcpOnly(block: => Unit): Unit = if (!useHttpClient) block
  def httpOnly(block: => Unit): Unit = if (useHttpClient) block
}
