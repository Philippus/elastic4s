package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.common.DocumentRef

case class TermsLookup(ref: DocumentRef, path: String, routing: Option[String] = None)
