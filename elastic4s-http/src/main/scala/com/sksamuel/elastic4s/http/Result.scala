package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.http.update.ElasticError

sealed trait Result[T] {
  def isSuccess = false
  def isThrowable = false
  def isError = false
  def value: T = throw new NoSuchElementException
  def status: Int
  def error: ElasticError = throw new NoSuchElementException
  def throwable: Throwable = throw new NoSuchElementException
}

case class RequestError(override val error: ElasticError, override val status: Int) extends Result[Nothing] {
  override def isError: Boolean = true
}

case class RequestThrowable(override val throwable: Throwable, override val status: Int) extends Result[Nothing] {
  override def isThrowable: Boolean = true
}

case class RequestSuccess[T](override val value: T, override val status: Int) extends Result[T] {
  override def isSuccess: Boolean = true
}
