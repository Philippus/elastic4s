package com.sksamuel.elastic4s.search.highlight

import com.sksamuel.elastic4s.ElasticDsl

object Examples extends ElasticDsl {

  search("music").query("kate bush").highlighting(
    highlight("body").fragmentSize(20)
  )

}
