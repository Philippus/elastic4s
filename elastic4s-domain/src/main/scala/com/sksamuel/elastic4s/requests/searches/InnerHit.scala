package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.get.{HitField, MetaDataFields}

case class InnerHit(index: String,
                    `type`: String,
                    id: String,
                    nested: Map[String, AnyRef],
                    score: Option[Double],
                    routing: String,
                    source: Map[String, AnyRef],
                    innerHits: Map[String, InnerHits],
                    highlight: Map[String, Seq[String]],
                    sort: Seq[AnyRef],
                    fields: Map[String, AnyRef]) {

  def docValueField(fieldName: String): HitField = docValueFieldOpt(fieldName).get
  def docValueFieldOpt(fieldName: String): Option[HitField] = fields.get(fieldName).map { v =>
    new HitField {
      override def values: Seq[AnyRef] = v match {
        case values: Seq[AnyRef] => values
        case value: AnyRef => Seq(value)
      }
      override def value: AnyRef = values.head
      override def name: String = fieldName
      override def isMetadataField: Boolean = MetaDataFields.fields.contains(name)
    }
  }

  // todo put back ?
//  def sourceAsString: String = SourceAsContentBuilder(source).string()
}
