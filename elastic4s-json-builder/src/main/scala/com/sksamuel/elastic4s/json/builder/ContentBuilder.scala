package com.sksamuel.elastic4s.json.builder

trait ContentFactory {

  /**
    * Returns a new [[ContentBuilder]] for building json structures.
    */
  def builder(): ContentBuilder

}

trait ContentBuilder {

  /**
    * Returns a JSON string generated from the contents of the builder
    */
  def string(): String
}
