package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.script.{DeleteStoredScriptRequest, GetStoredScriptRequest, PutStoredScriptRequest, StoredScriptSource}

trait StoredScriptApi {

  def getStoredScript(id: String): GetStoredScriptRequest = GetStoredScriptRequest(id)
  def deleteStoredScript(id: String): DeleteStoredScriptRequest = DeleteStoredScriptRequest(id)
  def putStoredScript(id: String, script: StoredScriptSource): PutStoredScriptRequest = PutStoredScriptRequest(id, script)
}
