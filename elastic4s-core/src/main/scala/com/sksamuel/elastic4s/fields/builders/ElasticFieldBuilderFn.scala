package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.{AliasField, BinaryField, BooleanField, CompletionField, ConstantKeywordField, DateField, DateNanosField, DenseVectorField, ElasticField, FlattenedField, GeoPointField, GeoShapeField, JoinField, KeywordField, NestedField, NumberField, ObjectField, RangeField, RankFeatureField, RankFeaturesField, SearchAsYouTypeField, TextField, TokenCountField}
import com.sksamuel.elastic4s.json.XContentBuilder

object ElasticFieldBuilderFn {

  def apply(field: ElasticField): XContentBuilder = {
    field match {
      case f: AliasField => AliasFieldBuilderFn.build(f)
      case f: BinaryField => BinaryFieldBuilderFn.build(f)
      case f: BooleanField => BooleanFieldBuilderFn.build(f)
      case f: ConstantKeywordField => ConstantKeywordFieldBuilderFn.build(f)
      case f: CompletionField => CompletionFieldBuilderFn.build(f)
      case f: DateField => DateFieldBuilderFn.build(f)
      case f: DateNanosField => DateNanosFieldBuilderFn.build(f)
      case f: DenseVectorField => DenseVectorFieldBuilderFn.build(f)
      case f: FlattenedField => FlattenedFieldBuilderFn.build(f)
      case f: GeoPointField => GeoPointFieldBuilderFn.build(f)
      case f: GeoShapeField => GeoShapeFieldBuilderFn.build(f)
      case f: JoinField => JoinFieldBuilderFn.build(f)
      case f: KeywordField => KeywordFieldBuilderFn.build(f)
      case f: NestedField => NestedFieldBuilderFn.build(f)
      case f: NumberField[_] => NumberFieldBuilderFn.build(f)
      case f: ObjectField => ObjectFieldBuilderFn.build(f)
      case f: RangeField => RangeFieldBuilderFn.build(f)
      case f: RankFeatureField => RankFeatureFieldBuilderFn.build(f)
      case f: RankFeaturesField => RankFeaturesFieldBuilderFn.build(f)
      case f: SearchAsYouTypeField => SearchAsYouTypeFieldBuilderFn.build(f)
      case f: TextField => TextFieldBuilderFn.build(f)
      case f: TokenCountField => TokenCountFieldBuilderFn.build(f)
    }
  }
}
