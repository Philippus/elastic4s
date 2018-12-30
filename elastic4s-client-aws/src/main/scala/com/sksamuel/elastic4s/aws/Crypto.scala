package com.sksamuel.elastic4s.aws

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Crypto {

  private[aws] val Algorithm = "AWS4-HMAC-SHA256"
  private val signAlgorithm  = "HmacSHA256"
  private val hashAlgorithm  = "SHA-256"

  private[aws] def hash(data: Array[Byte]): Array[Byte] = {
    val md = MessageDigest.getInstance(hashAlgorithm)
    md.update(data)
    md.digest()
  }

  private[aws] def hash(str: String): Array[Byte] = hash(str.getBytes("utf-8"))

  private[aws] def sign(data: Array[Byte], key: Array[Byte]): Array[Byte] = {
    val mac = Mac.getInstance(signAlgorithm)
    mac.init(new SecretKeySpec(key, signAlgorithm))
    mac.doFinal(data)
  }

  private[aws] def sign(str: String, key: Array[Byte]): Array[Byte] = sign(str.getBytes("utf-8"), key)

  private[aws] def hexOf(bytes: Array[Byte]): String = bytes.map("%02X" format _).mkString

}
