package com.sksamuel.elastic4s.http.validate

import cats.Show
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler, Shards}
import com.sksamuel.elastic4s.validate.ValidateDefinition
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

import scala.concurrent.Future

case class ValidateResponse(valid: Boolean,
                            private val _shards: Shards,
                            explanations: Seq[Explanation]) {
  def shards: Shards = _shards
  def isValid: Boolean = valid
}

case class Explanation(index: String,
                       valid: Boolean,
                       error: String)

object ValidateBodyFn {
  def apply(v: ValidateDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.rawField("query", QueryBuilderFn(v.query).bytes, XContentType.JSON)
    builder.endObject()
  }
}

trait ValidateImplicits {

  implicit object ValidateShow extends Show[ValidateDefinition] {
    override def show(v: ValidateDefinition): String = ValidateBodyFn(v).string()
  }

  implicit object ValidateHttpExecutable extends HttpExecutable[ValidateDefinition, ValidateResponse] {

    override def execute(client: RestClient,
                         request: ValidateDefinition): Future[ValidateResponse] = {

      val method = "GET"
      val endpoint = s"${request.indexesAndTypes.indexes.mkString(",")}/${request.indexesAndTypes.types.mkString(",")}/_validate/query"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.explain.map(_.toString).foreach(params.put("explain", _))
      request.rewrite.map(_.toString).foreach(params.put("rewrite", _))

      val body = ValidateBodyFn(request).string()
      logger.debug(s"Executing validate query $body")
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async(method, endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }
}
