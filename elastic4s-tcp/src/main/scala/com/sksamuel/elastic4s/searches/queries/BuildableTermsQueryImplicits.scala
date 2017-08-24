package com.sksamuel.elastic4s.searches.queries

import java.util.UUID

import com.sksamuel.elastic4s.searches.queries.term.{BuildableTermsQuery, TermsQueryDefinition}
import org.elasticsearch.index.query.{QueryBuilders, TermsQueryBuilder}

trait BuildableTermsQueryImplicits {

  implicit object UUIDBuildableTermsQuery extends BuildableTermsQuery[UUID] {
    override def build(q: TermsQueryDefinition[UUID]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.map(_.toString).toSeq: _*)
  }

  implicit object IntBuildableTermsQuery extends BuildableTermsQuery[Int] {
    override def build(q: TermsQueryDefinition[Int]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object LongBuildableTermsQuery extends BuildableTermsQuery[Long] {
    override def build(q: TermsQueryDefinition[Long]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object FloatBuildableTermsQuery extends BuildableTermsQuery[Double] {
    override def build(q: TermsQueryDefinition[Double]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object DoubleBuildableTermsQuery extends BuildableTermsQuery[Double] {
    override def build(q: TermsQueryDefinition[Double]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object StringBuildableTermsQuery extends BuildableTermsQuery[String] {
    override def build(q: TermsQueryDefinition[String]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object AnyRefBuildableTermsQuery extends BuildableTermsQuery[AnyRef] {
    override def build(q: TermsQueryDefinition[AnyRef]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.map(_.toString).toSeq: _*)
  }
}
