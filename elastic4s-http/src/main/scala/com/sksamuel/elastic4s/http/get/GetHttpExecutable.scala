package com.sksamuel.elastic4s.http.get

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.get.{GetDefinition, HitField}
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.exts.Logging
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._

object GetHttpExecutable extends HttpExecutable[GetDefinition, GetResponse] with Logging {

  override def execute(client: RestClient, request: GetDefinition): ResponseListener => Any = {
    val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}"
    val params = scala.collection.mutable.Map.empty[String, String]
    request.fetchSource.foreach { context =>
      if (!context.enabled) params.put("_source", "false")
    }
    if (request.storedFields.nonEmpty) {
      params.put("stored_fields", request.storedFields.mkString(","))
    }
    request.parent.foreach(params.put("parent", _))
    request.routing.foreach(params.put("routing", _))
    request.preference.foreach(params.put("preference", _))
    request.refresh.map(_.toString).foreach(params.put("refresh", _))
    request.realtime.map(_.toString).foreach(params.put("realtime", _))
    request.version.map(_.toString).foreach(params.put("version", _))
    request.versionType.foreach(params.put("versionType", _))
    logger.debug(s"Endpoint=$endpoint")
    client.performRequestAsync("GET", endpoint, params.asJava, _: ResponseListener)
  }
}

case class GetResponse(private val _id: String,
                       private val _index: String,
                       private val _type: String,
                       private val _version: Long,
                       found: Boolean,
                       private val fields: Map[String, Any],
                       private val _source: Map[String, Any]
                      ) {

  def index = _index
  def `type` = _type
  def id = _id
  def version = _version
  def ref = DocumentRef(index, `type`, id)
  def exists = found
  def source = sourceAsMap
  def storedField(fieldName: String): HitField = new HitField {
    override def values: Seq[AnyRef] = fields(fieldName) match {
      case values: Seq[AnyRef] => values
      case values: Array[AnyRef] => values
      case value: AnyRef => Seq(value)
    }
    override def value: AnyRef = values.head
    override def name: String = fieldName
    override def isMetadataField: Boolean = ???
  }
  def storedFieldsAsMap = Option(fields).getOrElse(Map.empty)
  def sourceAsMap = Option(_source).getOrElse(Map.empty)
}
