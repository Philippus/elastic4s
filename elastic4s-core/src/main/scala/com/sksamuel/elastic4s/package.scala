package com.sksamuel

package object elastic4s {
  @deprecated("HitReader is now just an alias for Readable[T]", "6.0.0")
  type HitReader[T] = Readable[T]
}
