package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.fields.ElasticField
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.{DynamicMapping, DynamicTemplateRequest}
import com.sksamuel.elastic4s.requests.searches.RuntimeMapping

trait MappingDefinitionLike {
  def all: Option[Boolean]
  def source: Option[Boolean]
  def sourceExcludes: Seq[String]
  def dateDetection: Option[Boolean]
  def numericDetection: Option[Boolean]
  def size: Option[Boolean]
  def dynamicDateFormats: Seq[String]
  def properties: Seq[ElasticField]
  def analyzer: Option[String]
  def boostName: Option[String]
  def boostNullValue: Option[Double]
  def parent: Option[String]
  def dynamic: Option[DynamicMapping]
  def meta: Map[String, Any]
  def routing: Option[Routing]
  def templates: Seq[DynamicTemplateRequest]
  def rawSource: Option[String]
  def runtimes: Seq[RuntimeMapping]
}
