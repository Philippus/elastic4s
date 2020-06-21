package com.sksamuel.elastic4s

sealed trait Response[+U] {

  /**
    * Returns the http status code of the response.
    */
  def status: Int

  /**
    * Returns the body included in the HTTP response or None if this response did not include a body.
    *
    * @return
    */
  def body: Option[String]

  /**
    * Returns any HTTP headers that were included in the response.
    */
  def headers: Map[String, String]

  /**
    * Returns the marshalled response U if this is an instance of [[RequestSuccess]], otherwise
    * throws an exception.
    */
  def result: U

  /**
    * Returns error details as an instance of [[ElasticError]] if this is [[RequestFailure]].
    * Otherwise throws an exception.
    */
  def error: ElasticError

  /**
    * Returns true if this response is an error state.
    *
    * @return
    */
  def isError: Boolean

  /**
    * Returns true if this response was successful
    */
  final def isSuccess: Boolean = !isError

  def map[V](f: U => V): Response[V]
  def flatMap[V](f: U => Response[V]): Response[V]

  final def fold[V](ifError: => V)(f: U => V): V = if (isError) ifError else f(result)
  final def fold[V](onError: RequestFailure => V, onSuccess: U => V): V = this match {
    case failure: RequestFailure => onError(failure)
    case RequestSuccess(_, _, _, result) => onSuccess(result)
  }
  final def foreach[V](f: U => V): Unit = if (!isError) f(result)

  final def toOption: Option[U] = if (isError) None else Some(result)
  final def toEither: Either[ElasticError, U] = if (isError) Left(error) else Right(result)
}

case class RequestSuccess[U](override val status: Int, // the http status code of the response
                             override val body: Option[String], // the http response body if the response included one
                             override val headers: Map[String, String], // any http headers included in the response
                             override val result: U) extends Response[U] {
  override def isError = false
  override def error = throw new NoSuchElementException(s"Request success $result")

  final def map[V](f: U => V): Response[V] = RequestSuccess(status, body, headers, f(result))
  final def flatMap[V](f: U => Response[V]): Response[V] = f(result)
}

case class RequestFailure(override val status: Int, // the http status code of the response
                          override val body: Option[String], // the http response body if the response included one
                          override val headers: Map[String, String], // any http headers included in the response
                          override val error: ElasticError) extends Response[Nothing] {
  override def result = throw new NoSuchElementException(s"Request Failure $error")
  override def isError = true
  final def map[V](f: Nothing => V): Response[V] = this
  final def flatMap[V](f: Nothing => Response[V]): Response[V] = this
}
