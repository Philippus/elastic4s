package com.sksamuel.elastic4s.ext

object OptionImplicits {

  implicit class RichOption[T](option: Option[T]) {
    def getOrError(message: String): T = option.getOrElse(sys.error(message))
  }

  /** Better than Some(t) because that will return the inferred type as Some[T], but in a fold we probably want the type
    * inferred as Option[T]
    */
  implicit class RichOptionImplicits[T](t: T) {
    def some: Option[T] = Some(t)
  }

  implicit class RichStringOptionImplicits(str: String) {
    def some: Option[String] = StringOption(str)
  }

  def none[T]: Option[T] = None
}

object StringOption {
  def apply(str: String): Option[String] = Option(str).filter(_.trim.nonEmpty)
}
