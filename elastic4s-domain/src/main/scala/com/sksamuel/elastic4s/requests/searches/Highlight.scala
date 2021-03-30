package com.sksamuel.elastic4s.requests.searches

case class Highlight(options: HighlightOptions, fields: Iterable[HighlightField])
