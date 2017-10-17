package com.sksamuel.elastic4s.aws

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Crypto {

  val Algorithm = "AWS4-HMAC-SHA256"
  val signAlgorithm = "HmacSHA256"
  val hashAlgorithm = "SHA-256"

  def hash(data: Array[Byte]): Array[Byte] = {
    val md = MessageDigest.getInstance(hashAlgorithm)
    md.update(data)
    md.digest()
  }

  def hash(str: String): Array[Byte] = hash(str.getBytes("utf-8"))

  def sign(data: Array[Byte], key: Array[Byte]): Array[Byte] = {
    val mac = Mac.getInstance(signAlgorithm)
    mac.init(new SecretKeySpec(key, signAlgorithm))
    mac.doFinal(data)
  }

  def sign(str: String, key: Array[Byte]): Array[Byte] = sign(str.getBytes("utf-8"), key)

  def hexOf(bytes: Array[Byte]): String = bytes.map("%02X" format _).mkString

}
