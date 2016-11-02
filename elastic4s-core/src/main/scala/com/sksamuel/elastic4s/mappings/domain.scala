package com.sksamuel.elastic4s.mappings

case class YesNo(value: String)
object YesNo {
  val Yes = YesNo("yes")
  val No = YesNo("no")
  def apply(param: Boolean): YesNo = param match {
    case true => Yes
    case false => No
  }
}

case class TermVector(value: String)
object TermVector {
  val No = TermVector("no")
  val Yes = TermVector("yes")
  val WithOffsets = TermVector("with_offsets")
  val WithPositions = TermVector("with_positions")
  val WithPositionsOffsets = TermVector("with_positions_offsets")
  val WithPositionsOffsetsPayloads = TermVector("with_positions_offsets_payloads")
}

case class IndexOptions(value: String)
object IndexOptions {
  val Docs = IndexOptions("docs")
  val Freqs = IndexOptions("freqs")
  val Positions = IndexOptions("positions")
  val Offsets = IndexOptions("offsets")
}

case class PostingsFormat(value: String)
object PostingsFormat {
  val Direct = PostingsFormat("direct")
  val Memory = PostingsFormat("memory")
  val Pulsing = PostingsFormat("pulsing")
  val BloomDefault = PostingsFormat("bloom_default")
  val BloomPulsing = PostingsFormat("bloom_pulsing")
  val Default = PostingsFormat("default")
}

case class DocValuesFormat(value: String)
object DocValuesFormat {
  val Memory = DocValuesFormat("memory")
  val Disk = DocValuesFormat("disk")
  val Default = DocValuesFormat("default")
}

case class Similarity(value: String)
object Similarity {
  val Default = Similarity("default")
  val BM25 = Similarity("BM25")
}

case class PrefixTree(value: String)
object PrefixTree {
  val Geohash = PrefixTree("geohash")
  val Quadtree = PrefixTree("quadtree")
}
