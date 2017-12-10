package controllers

import javax.inject._

import play.api.mvc._
import api._
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.{JsValue, Json}
import services.{TextProcessor, TypeTalkClient}


@Singleton
class BotController @Inject()(cc: ControllerComponents, typeTalkClient: TypeTalkClient, textProcessor: TextProcessor)
                             (implicit executionContext: ExecutionContext) extends AbstractController(cc)  {

  import Requests._
  import services.Language._


  def receiveMessage = Action.async(parse.json) { request: Request[JsValue]  =>
    Json.fromJson[Message](request.body).map { botRequest =>

      botRequest.post.replyTo.map { replyTo =>
          typeTalkClient.getMessage(botRequest.topic.id, replyTo).map { replyMessage =>
            val translation = textProcessor.translateText(replyMessage.post.message, English, Japanese)
            BotResponse(s"Got ${replyMessage.post.message}, translated to $translation", botRequest.post.id)
          }.recover {
            case ex =>
              Logger.error(s"Unable to find reply message due to exception", ex)
              BotResponse("I'm sorry - I couldn't find the message you wanted me to translate!", botRequest.post.id)
          }.map(response => Ok(Json.toJson(response)))
      }.getOrElse {
        val response = BotResponse(s"Thanks for the message ${botRequest.post.account.fullName}", botRequest.post.id)
        Future.successful(Ok(Json.toJson(response)))
      }
    }.getOrElse {
      Logger.error("Failed to parse incoming request to bot")
      Future.successful(BadRequest("Unable to parse bot request"))
    }

  }

}
