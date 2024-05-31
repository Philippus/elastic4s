package com.sksamuel.elastic4s.handlers.synonyms

import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.requests.synonyms.{DeleteSynonymRuleRequest, DeleteSynonymRuleResponse, DeleteSynonymsSetRequest, GetSynonymRuleRequest, GetSynonymRuleResponse, GetSynonymsSetRequest, GetSynonymsSetResponse, ListSynonymsSetRequest, ListSynonymsSetResponse, CreateOrUpdateSynonymRuleRequest, UpdateSynonymRuleResponse, CreateOrUpdateSynonymsSetRequest, UpdateSynonymsSetResponse}
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler}

trait SynonymsHandlers {
  implicit object UpdateSynonymsSetHandler extends Handler[CreateOrUpdateSynonymsSetRequest, UpdateSynonymsSetResponse] {

    override def responseHandler: ResponseHandler[UpdateSynonymsSetResponse] = new ResponseHandler[UpdateSynonymsSetResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, UpdateSynonymsSetResponse] = {
        response.statusCode match {
          case 200 | 201 => Right(ResponseHandler.fromResponse[UpdateSynonymsSetResponse](response))
          case 400 => Left(ElasticErrorParser.parse(response))
          case _ => sys.error("Invalid response")
        }
      }
    }

    override def build(request: CreateOrUpdateSynonymsSetRequest): ElasticRequest = {
      val endpoint = s"_synonyms/${request.synonymsSet}"

      val body = UpdateSynonymsBodyFn(request).string
      val entity = HttpEntity(body, "application/json")

      ElasticRequest("PUT", endpoint, entity)
    }
  }

  implicit object GetSynonymsSetHandler extends Handler[GetSynonymsSetRequest, GetSynonymsSetResponse] {
      override def responseHandler: ResponseHandler[GetSynonymsSetResponse] = new ResponseHandler[GetSynonymsSetResponse] {
        override def handle(response: HttpResponse): Either[ElasticError, GetSynonymsSetResponse] =
          response.statusCode match {
            case 200 => Right(ResponseHandler.fromResponse[GetSynonymsSetResponse](response))
            case 400 | 404  => Left(ElasticErrorParser.parse(response))
            case _ => sys.error("Invalid response")
          }
      }

      override def build(request: GetSynonymsSetRequest): ElasticRequest = {
        val endpoint = s"_synonyms/${request.synonymsSet}"
        val params = scala.collection.mutable.Map.empty[String, String]
        request.from.foreach(from => params.put("from", from.toString))
        request.size.foreach(size => params.put("size", size.toString))
        ElasticRequest("GET", endpoint, params.toMap)
      }
  }

  implicit object ListSynonymsSetHandler extends Handler[ListSynonymsSetRequest, ListSynonymsSetResponse] {
    override def responseHandler: ResponseHandler[ListSynonymsSetResponse] = new ResponseHandler[ListSynonymsSetResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, ListSynonymsSetResponse] =
        response.statusCode match {
          case 200 => Right(ResponseHandler.fromResponse[ListSynonymsSetResponse](response))
          case _ => sys.error("Invalid response")
        }
    }

    override def build(request: ListSynonymsSetRequest): ElasticRequest = {
      val endpoint = "_synonyms"
      val params = scala.collection.mutable.Map.empty[String, String]
      request.from.foreach(from => params.put("from", from.toString))
      request.size.foreach(size => params.put("size", size.toString))
      ElasticRequest("GET", endpoint, params.toMap)
    }
  }

  implicit object DeleteSynonymsSetHandler extends Handler[DeleteSynonymsSetRequest, Unit] {
    override def responseHandler: ResponseHandler[Unit] = new ResponseHandler[Unit] {
      override def handle(response: HttpResponse): Either[ElasticError, Unit] =
        response.statusCode match {
          case 200 => Right(())
          case 400 | 404 => Left(ElasticErrorParser.parse(response))
          case _ => sys.error("Invalid response")
        }
    }

    override def build(request: DeleteSynonymsSetRequest): ElasticRequest = {
      val endpoint = s"_synonyms/${request.synonymsSet}"
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object UpdateSynonymRuleHandler extends Handler[CreateOrUpdateSynonymRuleRequest, UpdateSynonymRuleResponse] {
    override def responseHandler: ResponseHandler[UpdateSynonymRuleResponse] = new ResponseHandler[UpdateSynonymRuleResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, UpdateSynonymRuleResponse] =
        response.statusCode match {
          case 200 | 201 => Right(ResponseHandler.fromResponse[UpdateSynonymRuleResponse](response))
          case 400 | 404 => Left(ElasticErrorParser.parse(response))
          case _ => sys.error("Invalid response")
        }
    }

    override def build(request: CreateOrUpdateSynonymRuleRequest): ElasticRequest = {
      val endpoint = s"_synonyms/${request.synonymsSet}/${request.synonymRule}"
      println(endpoint)
      val body = UpdateSynonymRuleBodyFn(request).string
      val entity = HttpEntity(body, "application/json")
      ElasticRequest("PUT", endpoint, entity)
    }
  }

  implicit object GetSynonymRuleHandler extends Handler[GetSynonymRuleRequest, GetSynonymRuleResponse] {
    override def responseHandler: ResponseHandler[GetSynonymRuleResponse] = new ResponseHandler[GetSynonymRuleResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, GetSynonymRuleResponse] =
        response.statusCode match {
          case 200 => Right(ResponseHandler.fromResponse[GetSynonymRuleResponse](response))
          case 404 => Left(ElasticErrorParser.parse(response))
          case _ => sys.error("Invalid response")
        }
    }

    override def build(request: GetSynonymRuleRequest): ElasticRequest = {
      val endpoint = s"_synonyms/${request.synonymsSet}/${request.synonymRule}"
      ElasticRequest("GET", endpoint)
    }
  }

  implicit object DeleteSynonymRuleHandler extends Handler[DeleteSynonymRuleRequest, DeleteSynonymRuleResponse] {
    override def responseHandler: ResponseHandler[DeleteSynonymRuleResponse] = new ResponseHandler[DeleteSynonymRuleResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, DeleteSynonymRuleResponse] =
        response.statusCode match {
          case 200 => Right(ResponseHandler.fromResponse[DeleteSynonymRuleResponse](response))
          case 404 => Left(ElasticErrorParser.parse(response))
          case _ => sys.error("Invalid response")
        }
    }

    override def build(request: DeleteSynonymRuleRequest): ElasticRequest = {
      val endpoint = s"_synonyms/${request.synonymsSet}/${request.synonymRule}"
      ElasticRequest("DELETE", endpoint)
    }
  }
}
