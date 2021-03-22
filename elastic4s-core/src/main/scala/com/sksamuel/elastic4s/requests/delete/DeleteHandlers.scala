package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.common.RefreshPolicyHttpValue
import com.sksamuel.elastic4s.requests.indexes.VersionTypeHttpString
import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, ElasticUrlEncoder, Handler, HttpEntity, HttpResponse, ResponseHandler, XContentBuilder, XContentFactory}

object DeleteByQueryBodyFn {
  def apply(request: DeleteByQueryRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", QueryBuilderFn(request.query))
    builder.endObject()
    builder
  }
}

trait DeleteHandlers {

  implicit object DeleteByQueryHandler extends Handler[DeleteByQueryRequest, DeleteByQueryResponse] {

    override def responseHandler: ResponseHandler[DeleteByQueryResponse] = new ResponseHandler[DeleteByQueryResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, DeleteByQueryResponse] =
        response.statusCode match {
          case 200 | 201 => Right(ResponseHandler.fromResponse[DeleteByQueryResponse](response))
          case _         => Left(ElasticError.parse(response))
        }
    }

    override def build(request: DeleteByQueryRequest): ElasticRequest = {

      val endpoint = s"/${request.indexes.values.map(ElasticUrlEncoder.encodeUrlFragment).mkString(",")}/_delete_by_query"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.proceedOnConflicts.getOrElse(false))
        params.put("conflicts", "proceed")
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.requestsPerSecond.map(_.toString).foreach(params.put("requests_per_second", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.scrollSize.map(_.toString).foreach(params.put("scroll_size", _))
      request.routing.map(_.toString).foreach(params.put("routing", _))
      request.size.map(_.toString).foreach(params.put("size", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      val body = DeleteByQueryBodyFn(request)
      logger.debug(s"Delete by query ${body.string()}")
      val entity = HttpEntity(body.string(), "application/json")

      ElasticRequest("POST", endpoint, params.toMap, entity)
    }
  }

  implicit object DeleteByIdHandler extends Handler[DeleteByIdRequest, DeleteResponse] {

    override def responseHandler: ResponseHandler[DeleteResponse] = new ResponseHandler[DeleteResponse] {

      override def handle(response: HttpResponse): Either[ElasticError, DeleteResponse] = {

        def right = Right(ResponseHandler.fromResponse[DeleteResponse](response))

        def left = Left(ElasticError.parse(response))

        response.statusCode match {
          case 200 | 201 => right
          // annoying, 404s can return different types of data for a delete
          case 404 =>
            val node = ResponseHandler.json(response.entity.get)
            if (node.has("error")) left else right
          case _ => left
        }
      }
    }

    override def build(request: DeleteByIdRequest): ElasticRequest = {

      val endpoint =
        s"/${ElasticUrlEncoder.encodeUrlFragment(request.index.index)}/_doc/${ElasticUrlEncoder.encodeUrlFragment(request.id)}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.ifPrimaryTerm.map(_.toString).foreach(params.put("if_primary_term", _))
      request.ifSeqNo.map(_.toString).foreach(params.put("if_seq_no", _))
      request.versionType.map(VersionTypeHttpString.apply).foreach(params.put("version_type", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      ElasticRequest("DELETE", endpoint, params.toMap)
    }
  }
}
