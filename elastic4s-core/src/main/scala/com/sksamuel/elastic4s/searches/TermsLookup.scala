package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.DocumentRef

case class TermsLookup(ref: DocumentRef, path: String, routing: Option[String] = None)
