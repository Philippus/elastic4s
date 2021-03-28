package com.sksamuel.elastic4s.handlers

trait ContentBuilder {

  /**
    * Generate a json string from the contents of the builder
    */
  def string(): String
}
