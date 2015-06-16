package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s.ElasticDsl

class SearchDotDsl extends ElasticDsl {

  // simple search for free text
  search("index" / "type").query("coldplay")

  // search for term only
  search("index" / "type").term("name", "sammy")

  // regex search
  search("index" / "type").regex("name", ".*sammy.*")

  // term search with name field highlighting, with html <strong> tags to surround the highlighted text
  search("index" / "type").term("name", "sammy").highlighting(highlight("name").preTag("<strong>").postTag("</strong>"))

  // bool search with one must (AND) and one should (OR)
  search("index" / "type").bool(must(termQuery("name", "sammy")).should(termQuery("place", "buckinghamshire")))

  // free text, with offset/start 10 and returning a max of 20 records
  search("index" / "type").query("coldplay").start(10).limit(20)

  // scroll search with 1 minute keepalive
  search("index" / "type").query("coldplay").scroll("1m")
  searchScroll("scrollIdFromPreviousResult").keepAlive("1m")
}
