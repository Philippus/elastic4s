package com.sksamuel

package object elastic4s {
  @deprecated("HitReader is now just an alias for Readable[T]")
  type HitReader[T] = Readable[T]
}
