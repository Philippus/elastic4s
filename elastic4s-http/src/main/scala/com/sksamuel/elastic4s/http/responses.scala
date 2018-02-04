package com.sksamuel.elastic4s.http

sealed trait Response[+U] {
  def status: Int                  // the http status code of the response
  def body: Option[String]         // the http response body if the response included one
  def headers: Map[String, String] // any http headers included in the response
  def result: U                    // returns the marshalled response U or throws an exception
  def error: ElasticError          // returns the error or throw an exception
  def isError: Boolean             // returns true if this is an error response
  final def isSuccess: Boolean                   = !isError // returns true if this is a success
  final def map[V](f: U => V): Option[V]         = if (isError) None else Some(f(result))
  final def fold[V](ifError: => V)(f: U => V): V = if (isError) ifError else f(result)
  final def foreach[V](f: U => V): Unit          = if (!isError) f(result)
}

case class RequestSuccess[U](override val status: Int, // the http status code of the response
                             override val body: Option[String], // the http response body if the response included one
                             override val headers: Map[String, String], // any http headers included in the response
                             override val result: U)
    extends Response[U] {
  override def isError = false
  override def error   = throw new NoSuchElementException(s"Request success $result")
}

case class RequestFailure(override val status: Int, // the http status code of the response
                          override val body: Option[String], // the http response body if the response included one
                          override val headers: Map[String, String], // any http headers included in the response
                          override val error: ElasticError)
    extends Response[Nothing] {
  override def result  = throw new NoSuchElementException(s"Request Failure $error")
  override def isError = true
}
