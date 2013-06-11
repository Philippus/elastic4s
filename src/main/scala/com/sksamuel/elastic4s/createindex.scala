package com.sksamuel.elastic4s

/** @author Stephen Samuel */
case class CreateIndexReq(settings: IndexSettings, mappings: Seq[Mapping] = Nil)
case class IndexSettings(number_of_shards: Int = 1, number_of_replicas: Int = 1)
case class Mapping(name: String, fields: Seq[FieldMapping] = Nil)
case class FieldMapping(name: String, `type`: FieldType, analyzer: Analyzer, store: Boolean)

sealed trait Analyzer
object Analyzer {
    case object NotAnalyzed extends Analyzer
    case object Whitespace extends Analyzer
    case object Standard extends Analyzer
    case object Simple extends Analyzer
    case object Stop extends Analyzer
    case object Keyword extends Analyzer
    case object Pattern extends Analyzer
    case object Snowball extends Analyzer
}

sealed trait Tokenizer
object Tokenizer {
    case object Keyword extends Tokenizer
    case object Whitespace extends Tokenizer
    case object Standard extends Tokenizer
    case object Letter extends Tokenizer
}
