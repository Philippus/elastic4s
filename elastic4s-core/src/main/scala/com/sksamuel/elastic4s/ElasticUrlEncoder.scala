package com.sksamuel.elastic4s

import java.net.URLEncoder

object ElasticUrlEncoder {
  def encodeUrlFragment(fragment: String): String = URLEncoder.encode(fragment, "UTF-8").replace("+", "%20")
}
