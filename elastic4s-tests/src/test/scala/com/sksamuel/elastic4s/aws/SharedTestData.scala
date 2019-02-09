package com.sksamuel.elastic4s.aws

import java.io.ByteArrayInputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.entity.BasicHttpEntity
import software.amazon.awssdk.regions.Region

trait SharedTestData {

  val region  = Region.of("us-east-1")
  val service = "es"
  val dateTime = "20150830T123600Z"
  val date = "20150830"
  val host = "es.amazonaws.com"
  val awsKey = "AKIDEXAMPLE"
  val awsSecret = "YNexysRYkuJmLzyNKfotrkEEWWwTEiOgXPEHHGsp"
  val awsSessionToken = "ThisIsASessionToken"
  val forbiddenCharactersAndMore =  URLEncoder.encode("!@#$%ˆ&*()/to[]{};'", "UTF-8")
  val encodedForbiddenCharactersAndMore = "%2521%2540%2523%2524%2525%25CB%2586%2526%2A%2528%2529%252Fto%255B%255D%257B%257D%253B%2527"

  def httpWithForbiddenCharacters = {
    val request = new HttpGet(s"https://es.amazonaws.com/path/to/resource${forbiddenCharactersAndMore}?Action=ListUsers&Version=2010-05-08")
    request.addHeader("x-amz-date", dateTime)
    request.addHeader("Host", host)
    request.addHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8")
    request
  }

  def httpGetRequest = {
    val request = new HttpGet("https://es.amazonaws.com/path/to/resource?Action=ListUsers&Version=2010-05-08")
    request.addHeader("x-amz-date", dateTime)
    request.addHeader("Host", host)
    request.addHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8")
    request
  }

  def httpGetRequestWithUnorderedQueryParams = {
    val request = new HttpGet("https://es.amazonaws.com/path/to/resource?Version=2010-05-08&Action=ListUsers")
    request.addHeader("x-amz-date", dateTime)
    request.addHeader("Host", host)
    request.addHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8")
    request
  }

  def httpPostRequest = {
    val entity = new BasicHttpEntity()
    entity.setContent(new ByteArrayInputStream("This is the content".getBytes(StandardCharsets.UTF_8.name())))
    val request = new HttpPost("https://es.amazonaws.com/path/to/resource?Action=ListUsers&Version=2010-05-08")

    request.setEntity(entity)
    request.addHeader("x-amz-date", dateTime)
    request.addHeader("Host", host)
    request.addHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8")
    request
  }

  def httpPostRequestWithoutDate = {
    val entity = new BasicHttpEntity()
    entity.setContent(new ByteArrayInputStream("This is the content".getBytes(StandardCharsets.UTF_8.name())))
    val request = new HttpPost("https://es.amazonaws.com/path/to/resource?Action=ListUsers&Version=2010-05-08")

    request.setEntity(entity)
    request.addHeader("Host", host)
    request.addHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8")
    request
  }

  def httpPostRequestWithBadHost = {
    val entity = new BasicHttpEntity()
    entity.setContent(new ByteArrayInputStream("This is the content".getBytes(StandardCharsets.UTF_8.name())))
    val request = new HttpPost("https://es.amazonaws.com:443/path/to/resource?Action=ListUsers&Version=2010-05-08")

    request.setEntity(entity)
    request.addHeader("Host", host)
    request.addHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8")
    request
  }

}
