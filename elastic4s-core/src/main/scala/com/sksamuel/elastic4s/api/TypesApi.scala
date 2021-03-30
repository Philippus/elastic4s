package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.fields.{BinaryField, BooleanField, ByteField, CompletionField, DateField, DoubleField, FloatField, GeoPointField, GeoShapeField, HalfFloatField, IntegerField, IpField, JoinField, KeywordField, LongField, NestedField, ObjectField, ScaledFloatField, SearchAsYouTypeField, ShortField, TextField, WildcardField}
import com.sksamuel.elastic4s.requests.script.{Script, ScriptField}

trait TypesApi {

  def keywordField(name: String): KeywordField            = KeywordField(name)
  def textField(name: String): TextField                  = TextField(name)
  def wildcardField(name: String): WildcardField          = WildcardField(name)
  def searchAsYouType(name: String): SearchAsYouTypeField = SearchAsYouTypeField(name)

  def byteField(name: String)        = ByteField(name)
  def doubleField(name: String)      = DoubleField(name)
  def floatField(name: String)       = FloatField(name)
  def halfFloatField(name: String)   = HalfFloatField(name)
  def intField(name: String)         = IntegerField(name)
  def longField(name: String)        = LongField(name)
  def booleanField(name: String)     = BooleanField(name)
  def scaledFloatField(name: String) = ScaledFloatField(name)
  def shortField(name: String)       = ShortField(name)

  def dateField(name: String) = DateField(name)
  def geopointField(name: String) = GeoPointField(name)
  def geoshapeField(name: String) = GeoShapeField(name)
  def scriptField(name: String, script: String) = ScriptField(name, Script(script))
  def scriptField(name: String, script: Script) = ScriptField(name, script)
  def nestedField(name: String) = NestedField(name)
  def objectField(name: String) = ObjectField(name)
  def ipField(name: String) = IpField(name)
  def joinField(name: String) = JoinField(name)
  def binaryField(name: String) = BinaryField(name)
  def completionField(name: String) = CompletionField(name)
}
