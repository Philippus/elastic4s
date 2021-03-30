package com.sksamuel.elastic4s.requests.exists

import com.sksamuel.elastic4s.Index

case class ExistsRequest(id: String, index: Index)
