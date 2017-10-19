package com.sksamuel.elastic4s.aws

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.entity.BasicHttpEntity

trait SharedTestData {

  val region  = "us-east-1"
  val service = "es"
  val dateTime = "20150830T123600Z"
  val date = "20150830"
  val host = "es.amazonaws.com"
  val awsKey = "AKIDEXAMPLE"
  val awsSecret = "YNexysRYkuJmLzyNKfotrkEEWWwTEiOgXPEHHGsp"
  val awsSessionToken = "ThisIsASessionToken"

  val httpGetRequest = {
    val request = new HttpGet("https://es.amazonaws.com/path/to/resource?Action=ListUsers&Version=2010-05-08")
    request.addHeader("x-amz-date", dateTime)
    request.addHeader("host", host)
    request.addHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8")
    request
  }

  val httpPostRequest = {
    val entity = new BasicHttpEntity()
    entity.setContent(new ByteArrayInputStream("This is the content".getBytes(StandardCharsets.UTF_8.name())))
    val request = new HttpPost("https://es.amazonaws.com/path/to/resource?Action=ListUsers&Version=2010-05-08")

    request.setEntity(entity)
    request.addHeader("x-amz-date", dateTime)
    request.addHeader("host", host)
    request.addHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8")
    request
  }

}
