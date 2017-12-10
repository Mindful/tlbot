package services

import javax.inject.{Inject, Singleton}

import api._
import play.api.Configuration
import play.api.http.{ContentTypes, HeaderNames}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TypeTalkClient @Inject() (ws: WSClient, config: Configuration)(implicit executionContext: ExecutionContext) extends ContentTypes with HeaderNames {

  private val baseUrl = config.get[String]("typetalk.url")
  private val token = config.get[String]("typetalk.token")
  import Requests._

  def getMessage(topicId: Int, postId: Int): Future[Message] = {
    val url = s"$baseUrl/topics/$topicId/posts/$postId?typetalkToken=$token"

    ws.url(url).addHttpHeaders(
      CONTENT_TYPE -> JSON,
      ACCEPT -> JSON
    ).get().map { rawResponse =>
      Json.fromJson[Message](Json.parse(rawResponse.body)).getOrElse {
        throw new RuntimeException(s"Could not parse json ${rawResponse.body}")
      }
    }
  }

}
