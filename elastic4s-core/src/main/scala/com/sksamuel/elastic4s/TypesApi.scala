package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings._
import com.sksamuel.elastic4s.script.ScriptFieldDefinition

trait TypesApi {

  def field(name: String, ft: AttachmentType.type) = new AttachmentFieldDefinition(name)
  def field(name: String, ft: BinaryType.type) = new BinaryFieldDefinition(name)
  def field(name: String, ft: BooleanType.type) = new BooleanFieldDefinition(name)
  def field(name: String, ft: ByteType.type) = new ByteFieldDefinition(name)
  def field(name: String, ft: CompletionType.type) = new CompletionFieldDefinition(name)
  def field(name: String, ft: DateType.type) = new DateFieldDefinition(name)
  def field(name: String, ft: DoubleType.type) = new DoubleFieldDefinition(name)
  def field(name: String, ft: FloatType.type) = new FloatFieldDefinition(name)
  def field(name: String, ft: GeoPointType.type) = new GeoPointFieldDefinition(name)
  def field(name: String, ft: GeoShapeType.type) = new GeoShapeFieldDefinition(name)
  def field(name: String, ft: IntegerType.type) = new IntegerFieldDefinition(name)
  def field(name: String, ft: IpType.type) = new IpFieldDefinition(name)
  def field(name: String, ft: KeywordType.type) = new KeywordFieldDefinition(name)
  def field(name: String, ft: LongType.type) = new LongFieldDefinition(name)
  def field(name: String, ft: NestedType.type): NestedFieldDefinition = new NestedFieldDefinition(name)
  def field(name: String, ft: ObjectType.type): ObjectFieldDefinition = new ObjectFieldDefinition(name)
  def field(name: String, ft: PercolatorType.type): PercolatorFieldDefinition = new PercolatorFieldDefinition(name)
  def field(name: String, ft: ShortType.type) = new ShortFieldDefinition(name)
  def field(name: String, ft: TextType.type) = new TextFieldDefinition(name)
  def field(name: String, ft: TokenCountType.type) = new TokenCountDefinition(name)

  // -- helper methods to create the field definitions --
  def attachmentField(name: String) = field(name, AttachmentType)
  def binaryField(name: String) = field(name, BinaryType)
  def booleanField(name: String) = field(name, BooleanType)
  def byteField(name: String) = field(name, ByteType)
  def completionField(name: String) = field(name, CompletionType)
  def dateField(name: String) = field(name, DateType)
  def doubleField(name: String) = field(name, DoubleType)
  def floatField(name: String) = field(name, FloatType)
  def geopointField(name: String) = field(name, GeoPointType)
  def geoshapeField(name: String) = field(name, GeoShapeType)
  def intField(name: String) = field(name, IntegerType)
  def ipField(name: String) = field(name, IpType)
  def keywordField(name: String) = field(name, KeywordType)
  def longField(name: String) = field(name, LongType)
  def nestedField(name: String): NestedFieldDefinition = field(name, NestedType)
  def objectField(name: String): ObjectFieldDefinition = field(name, ObjectType)
  def percolatorField(name: String) = field(name, PercolatorType)
  def shortField(name: String) = field(name, ShortType)
  def textField(name: String): TextFieldDefinition = field(name, TextType)
  def tokenCountField(name: String) = field(name, TokenCountType)

  def scriptField(name: String, script: String): ScriptFieldDefinition = ScriptFieldDefinition(name, script, None, None)
  def scriptField(n: String): ExpectsScript = ExpectsScript(field = n)
  case class ExpectsScript(field: String) {
    def script(script: String): ScriptFieldDefinition = ScriptFieldDefinition(field, script, None, None)
  }

  @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
  def stringField(name: String): StringFieldDefinition = field(name, StringType)

  @deprecated("use field(name, type)", "5.0.0")
  def field(name: String): FieldDefinition = FieldDefinition(name)

  @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
  def field(name: String, ft: StringType.type) = new StringFieldDefinition(name)
}
