package com.sksamuel.elastic4s.requests.ingest

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

/**
  * Abstract representation of a processor with a constant name (e.g. "geoip") and an [[XContentBuilder]] that
  * constructs the body (e.g. { "field" : "ip" } ).
  */
trait Processor {
  def name: String
  def buildProcessorBody(): XContentBuilder
}

/**
  * Processor defined by its name and raw Json options.
  */
case class CustomProcessor(name: String, rawJsonOptions: String) extends Processor {
  override def buildProcessorBody(): XContentBuilder = XContentFactory.parse(rawJsonOptions)
}


/**
  * Processor that enriches an IP address with geographical information.
  * See docs for options: https://www.elastic.co/guide/en/elasticsearch/reference/current/geoip-processor.html
  */
case class GeoIPProcessor(field: String, targetField: Option[String] = None, databaseFile: Option[String] = None,
                          properties: Option[Seq[String]] = None, ignoreMissing: Option[Boolean] = None,
                          firstOnly: Option[Boolean] = None) extends Processor {
  override def name: String = GeoIPProcessor.name
  override def buildProcessorBody(): XContentBuilder = {
    val xcb = XContentFactory.jsonBuilder()
    xcb.field("field", field)
    targetField.foreach(xcb.field("target_field", _))
    databaseFile.foreach(xcb.field("database_file", _))
    properties.foreach(p => xcb.array("properties", p.toArray))
    ignoreMissing.foreach(xcb.field("ignore_missing", _))
    firstOnly.foreach(xcb.field("first_only", _))
    xcb
  }
}

object GeoIPProcessor {
  val name = "geoip"
}

