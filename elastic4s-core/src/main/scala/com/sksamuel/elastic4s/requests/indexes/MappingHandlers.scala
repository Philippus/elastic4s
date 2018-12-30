package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.mappings.{GetMappingRequest, PutMappingRequest}
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, IndexesAndTypes, ResponseHandler}

case class IndexMappings(index: String, mappings: Map[String, Map[String, Any]])

trait MappingHandlers {

  implicit object GetMappingHandler extends Handler[GetMappingRequest, Seq[IndexMappings]] {

    override def responseHandler: ResponseHandler[Seq[IndexMappings]] = new ResponseHandler[Seq[IndexMappings]] {
      override def handle(response: HttpResponse) : Either[ElasticError, Seq[IndexMappings]] = response.statusCode match {
        case 201 | 200       =>
          val raw = ResponseHandler.fromResponse[Map[String, Map[String, Map[String, Map[String, Any]]]]](response)
          val raw2 = raw.map {
            case (index, types) =>
              val mappings = types("mappings").map {
                case (tpe, properties) =>
                  tpe ->properties.get("properties").map(_.asInstanceOf[Map[String, Any]]).getOrElse(Map.empty)
                }
                IndexMappings(index, mappings)
          }.toSeq
          Right(raw2)
        case _              =>
          try {
            Left(ElasticError.parse(response))
          }catch{
            case _ : Throwable => sys.error(s"""Failed to parse error response: \n${response.toString}""")
          }
      }
    }


    override def build(request: GetMappingRequest): ElasticRequest = {
      val endpoint = request.indexesAndTypes match {
        case IndexesAndTypes(Nil, Nil)       => "/_mapping"
        case IndexesAndTypes(indexes, Nil)   => s"/${indexes.mkString(",")}/_mapping"
        case IndexesAndTypes(indexes, types) => s"/${indexes.mkString(",")}/_mapping/${types.mkString(",")}"
      }
      ElasticRequest("GET", endpoint)
    }
  }

  implicit object PutMappingHandler extends Handler[PutMappingRequest, PutMappingResponse] {

    override def build(request: PutMappingRequest): ElasticRequest = {

      val endpoint = s"/${request.indexesAndType.indexes.mkString(",")}/_mapping/${request.indexesAndType.`type`}"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.updateAllTypes.foreach(params.put("update_all_types", _))
      request.ignoreUnavailable.foreach(params.put("ignore_unavailable", _))
      request.allowNoIndices.foreach(params.put("allow_no_indices", _))
      request.expandWildcards.foreach(params.put("expand_wildcards", _))

      val body   = PutMappingBuilderFn(request).string()
      val entity = HttpEntity(body, "application/json")

      ElasticRequest("PUT", endpoint, params.toMap, entity)
    }
  }
}

case class PutMappingResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}
