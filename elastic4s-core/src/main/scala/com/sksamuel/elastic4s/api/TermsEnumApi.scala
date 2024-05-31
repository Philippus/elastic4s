package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.termsenum.TermsEnumRequest
trait TermsEnumApi {
  def termsEnum(index: String, field: String): TermsEnumRequest = TermsEnumRequest(Indexes(index), field)
  def termsEnum(indexes: Indexes, field: String): TermsEnumRequest = TermsEnumRequest(indexes, field)
  def termsEnum(indexes: Seq[String], field: String): TermsEnumRequest = TermsEnumRequest(Indexes(indexes), field)

  def termsEnum(index: String, field: String, string: String): TermsEnumRequest = TermsEnumRequest(Indexes(index), field, string = Some(string))
  def termsEnum(indexes: Indexes, field: String, string: String): TermsEnumRequest = TermsEnumRequest(indexes, field, string = Some(string))
  def termsEnum(indexes: Seq[String], field: String, string: String): TermsEnumRequest = TermsEnumRequest(Indexes(indexes), field, string = Some(string))
}
