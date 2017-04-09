package com.sksamuel.elastic4s.mappings.dynamictemplate

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings._
import com.sksamuel.elastic4s.script.ScriptFieldDefinition

trait DynamicTemplateApi {
  self: ElasticApi =>

  def dynamicTemplate(name: String) = new DynamicTemplateExpectsMapping(name)
  class DynamicTemplateExpectsMapping(name: String) {
    def mapping(fielddef: FieldDefinition): DynamicTemplateDefinition = DynamicTemplateDefinition(name, fielddef)
  }

  def dynamicTemplate(nameOfTemplate: String, mapping: FieldDefinition): DynamicTemplateDefinition = {
    DynamicTemplateDefinition(nameOfTemplate, mapping)
  }

  def dynamicType(): BasicFieldDefinition = BasicFieldDefinition("", "{dynamic_type}")
  def dynamicBinaryField(): BasicFieldDefinition = BasicFieldDefinition("", "binary")
  def dynamicBooleanField(): BasicFieldDefinition = BasicFieldDefinition("", "boolean")
  def dynamicByteField(): BasicFieldDefinition = BasicFieldDefinition("", "byte")
  def dynamicCompletionField(): CompletionFieldDefinition = CompletionFieldDefinition("")
  def dynamicDateField(): BasicFieldDefinition = BasicFieldDefinition("", "date")
  def dynamicDoubleField(): BasicFieldDefinition = BasicFieldDefinition("", "double")
  def dynamicFloatField(): BasicFieldDefinition = BasicFieldDefinition("", "float")
  def dynamicHalfFloatField(): BasicFieldDefinition = BasicFieldDefinition("", "half_float")
  def dynamicScaledFloatField(): BasicFieldDefinition = BasicFieldDefinition("", "scaled_float")
  def dynamicGeopointField(): BasicFieldDefinition = BasicFieldDefinition("", "geo_point")
  def dynamicGeoshapeField(): GeoshapeFieldDefinition = GeoshapeFieldDefinition("")
  def dynamicIntField(): BasicFieldDefinition = BasicFieldDefinition("", "integer")
  def dynamicIpField(): BasicFieldDefinition = BasicFieldDefinition("", "ip")
  def dynamicKeywordField(): KeywordFieldDefinition = KeywordFieldDefinition("")
  def dynamicLongField(): BasicFieldDefinition = BasicFieldDefinition("", "long")
  def dynamicNestedField(): NestedFieldDefinition = NestedFieldDefinition("")
  def dynamicObjectField(): ObjectFieldDefinition = ObjectFieldDefinition("")
  def dynamicPercolatorField(): BasicFieldDefinition = BasicFieldDefinition("", "percolator")
  def dynamicScriptField(script: String): ScriptFieldDefinition = ScriptFieldDefinition("", script, None, None)
  def dynamicShortField(): BasicFieldDefinition = BasicFieldDefinition("", "short")
  def dynamicTextField(): TextFieldDefinition = TextFieldDefinition("")
  def dynamicTokenCountField(): BasicFieldDefinition = BasicFieldDefinition("", "token_count")

  @deprecated("use dynamicIntField(), dynamicTextField() and so on", "5.2.12")
  def dynamicTemplateMapping(fieldType: FieldType): FieldDefinition = fieldType match {
    case BinaryType => binaryField("")
    case BooleanType => booleanField("")
    case ByteType => byteField("")
    case CompletionType => completionField("")
    case DateType => dateField("")
    case DoubleType => doubleField("")
    case FloatType => floatField("")
    case IntegerType => intField("")
    case IpType => ipField("")
    case GeoPointType => geopointField("")
    case GeoShapeType => geoshapeField("")
    case LongType => longField("")
    case NestedType => nestedField("")
    case ObjectType => objectField("")
    case ShortType => shortField("")
    case StringType => stringField("")
    case TextType => textField("")
    case TokenCountType => tokenCountField("")
  }
}
