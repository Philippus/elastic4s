package com.sksamuel.elastic4s

import org.elasticsearch.action._
import org.elasticsearch.action.admin.indices.IndicesAction

sealed trait RequestDefinitionLike[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]] {
  def action: GenericAction[Req, Res]
  def build: Req
}

abstract class RequestDefinition[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]]
(val action: Action[Req, Res, Builder]) extends RequestDefinitionLike[Req, Res, Builder]

abstract class IndicesRequestDefinition[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]]
(val action: IndicesAction[Req, Res, Builder]) extends RequestDefinitionLike[Req, Res, Builder]
