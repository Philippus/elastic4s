package com.sksamuel.elastic4s.http.explain

import com.sksamuel.elastic4s.DocumentRef

case class Explanation(value: Double,
                       description: String,
                       details: Seq[Explanation])

case class ExplainResponse(_index: String,
                           _type: String,
                           _id: String,
                           matched: Boolean,
                           explanation: Explanation) {

  def isMatch: Boolean = matched

  def ref: DocumentRef = DocumentRef(_index, _type, _id)
  def index: String = _index
  def `type`: String = _type
  def id: String = _id
}
