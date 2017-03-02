package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.mappings.FieldType._

trait DynamicTemplateApi {
  self: ElasticApi =>

  def dynamicTemplate(nameOfTemplate: String) = new DynamicTemplateExpectsMapping(nameOfTemplate)
  class DynamicTemplateExpectsMapping(nameOfTemplate: String) {
    def mapping(mapping: TypedFieldDefinition) = DynamicTemplateDefinition(nameOfTemplate, mapping)
  }

  def dynamicTemplate(nameOfTemplate: String, mapping: TypedFieldDefinition): DynamicTemplateDefinition = {
    DynamicTemplateDefinition(nameOfTemplate, mapping)
  }

  def dynamicTemplateMapping(fieldType: FieldType): TypedFieldDefinition = fieldType match {
    case AttachmentType => attachmentField("")
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
    case TokenCountType => tokenCountField("")
  }
}
