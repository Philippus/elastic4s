package com.sksamuel.elastic4s.streams

import com.sksamuel.elastic4s.BulkResult

class HasFailuresException(message:String, val bulkResult: BulkResult) extends Exception(message) {
  override def toString: String = {
    message + " " +
    "Failure messages: " +
    bulkResult.failures.map(f=> f.failureMessage).mkString( ",") +
    "."
  }
}
