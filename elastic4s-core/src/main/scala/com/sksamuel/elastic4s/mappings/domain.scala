package com.sksamuel.elastic4s.mappings

/** @author Stephen Samuel */
case class YesNo(val value: String)
object YesNo {
  case object Yes extends YesNo("yes")
  case object No extends YesNo("no")
  def apply(param: Boolean): YesNo = param match {
    case true => Yes
    case false => No
  }
}

case class TermVector(val value: String)
object TermVector {
  case object No extends TermVector("no")
  case object Yes extends TermVector("yes")
  case object WithOffsets extends TermVector("with_offsets")
  case object WithPositions extends TermVector("with_positions")
  case object WithPositionsOffsets extends TermVector("with_positions_offsets")
  case object WithPositionsOffsetsPayloads extends TermVector("with_positions_offsets_payloads")
}

case class IndexOptions(val value: String)
object IndexOptions {
  case object Docs extends IndexOptions("docs")
  case object Freqs extends IndexOptions("freqs")
  case object Positions extends IndexOptions("positions")
  case object Offsets extends IndexOptions("offsets")
}

case class PostingsFormat(val value: String)
object PostingsFormat {
  case object Direct extends PostingsFormat("direct")
  case object Memory extends PostingsFormat("memory")
  case object Pulsing extends PostingsFormat("pulsing")
  case object BloomDefault extends PostingsFormat("bloom_default")
  case object BloomPulsing extends PostingsFormat("bloom_pulsing")
  case object Default extends PostingsFormat("default")
}

case class DocValuesFormat(val value: String)
object DocValuesFormat {
  case object Memory extends DocValuesFormat("memory")
  case object Disk extends DocValuesFormat("disk")
  case object Default extends DocValuesFormat("default")
}

case class Similarity(val value: String)
object Similarity {
  case object Default extends Similarity("default")
  case object BM25 extends Similarity("BM25")
}

case class PrefixTree(val value: String)
object PrefixTree {
  case object Geohash extends PrefixTree("geohash")
  case object Quadtree extends PrefixTree("quadtree")
}
