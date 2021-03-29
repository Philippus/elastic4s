package com.sksamuel.elastic4s.requests.mappings

object TermVector {
  val No                           = "no"
  val Yes                          = "yes"
  val WithOffsets                  = "with_offsets"
  val WithPositions                = "with_positions"
  val WithPositionsOffsets         = "with_positions_offsets"
  val WithPositionsOffsetsPayloads = "with_positions_offsets_payloads"
}

object IndexOptions {
  val Docs      = "docs"
  val Freqs     = "freqs"
  val Positions = "positions"
  val Offsets   = "offsets"
}

object DocValuesFormat {
  val Memory  = "memory"
  val Disk    = "disk"
  val Default = "default"
}

object Similarity {
  val Default = "default"
  val BM25    = "BM25"
}

object PrefixTree {
  val Geohash  = "geohash"
  val Quadtree = "quadtree"
}
