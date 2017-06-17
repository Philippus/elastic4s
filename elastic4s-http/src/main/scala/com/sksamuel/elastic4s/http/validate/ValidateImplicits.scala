package com.sksamuel.elastic4s.http.validate

import cats.Show
import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http.values.Shards
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.validate.ValidateDefinition
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, RestClient}

import scala.concurrent.Future

case class ValidateResponse(valid: Boolean,
                            @JsonProperty("_shards") shards: Shards,
                            explanations: Seq[Explanation]) {
  def isValid: Boolean = valid
}

case class Explanation(index: String,
                       valid: Boolean,
                       error: String)

object ValidateBodyFn {
  def apply(v: ValidateDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", QueryBuilderFn(v.query))
    builder.endObject()
  }
}

trait ValidateImplicits {

  implicit object ValidateShow extends Show[ValidateDefinition] {
    override def show(v: ValidateDefinition): String = ValidateBodyFn(v).string()
  }

  implicit object ValidateHttpExecutable extends HttpExecutable[ValidateDefinition, ValidateResponse] {

    override def execute(client: RestClient, request: ValidateDefinition): Future[Response] = {

      val endpoint = s"${request.indexesAndTypes.indexes.mkString(",")}/${request.indexesAndTypes.types.mkString(",")}/_validate/query"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.explain.map(_.toString).foreach(params.put("explain", _))
      request.rewrite.map(_.toString).foreach(params.put("rewrite", _))

      val body = ValidateBodyFn(request).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async("GET", endpoint, params.toMap, entity)
    }
  }
}
