package com.sksamuel.elastic4s.requests.script

case class GetStoredScriptResponse(
    _id: String,
    found: Boolean,
    script: StoredScriptSource
)
