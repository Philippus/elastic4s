package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s.ElasticDsl

// examples of the count API in dot notation
class UpdateSqlDsl extends ElasticDsl {

  // update a doc by id in a given index / type.specifying a replacement doc
  update id "id" in "index" / "type" doc Map("name" -> "sam", "occuptation" -> "build breaker")

  // update a doc by id in a given index / type specifying a replacement doc with the optimistic locking set
  update id "id" in "index" / "type" version 3 doc("name" -> "sam", "occuptation" -> "build breaker")

  // update by id, using an implicit indexable for the updated doc
  val somedoc = Document("a", "b", "c")
  update id "id" in "index" / "type" source somedoc

  // update by id, using a script
  update id "id" in "index" / "type" script {
    script("ctx._source.somefield = 'value'")
  }

  // update by id, using a script, where the script language is specified
  update id "id" in "index" / "type" script {
    script("ctx._source.somefield = 'value'").lang("sillylang")
  }

  // update by id, using a script which has parameters which are passed in as a map
  update id "id" in "index" / "type" script {
    script("ctx._source.somefield = myparam").params("myparam" -> "sam")
  }
}

