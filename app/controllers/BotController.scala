package controllers

import javax.inject._

import api._
import clients.{GoogleTranslateClient, Language, TypeTalkClient}
import play.api.Logger
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class BotController @Inject()(cc: ControllerComponents, typeTalkClient: TypeTalkClient, translateClient: GoogleTranslateClient, messagesApi: MessagesApi)
                             (implicit executionContext: ExecutionContext) extends AbstractController(cc)  {

  import Requests._

  //User IDs can't be JP characters (I checked), so it's @<english characters><optional +><positive lookahead for space or JP characters>
  private val atUserRegex = "@[a-zA-Z]+\\+*(?=[\\s^\\u0000-\\u007F])"

  def receiveMessage = Action.async(parse.json) { request: Request[JsValue]  =>
    Json.fromJson[Message](request.body).map { botRequest =>
      val replyToId = botRequest.post.id

      botRequest.post.replyTo.map { replyTo =>

          typeTalkClient.getMessage(botRequest.topic.id, replyTo).flatMap { replyMessage =>
            computeTranslationResult(replyMessage.post.message).map { output =>
              BotResponse(output, Some(replyToId))
            }
          }.recover {
            case ex =>
              Logger.error(s"Could not find or translate replied to message due to exception", ex)
              BotResponse("I'm sorry - I couldn't find the message you wanted me to translate!", Some(replyToId))
          }

      }.getOrElse {

        computeTranslationResult(botRequest.post.message).map { output =>
          BotResponse(output, Some(replyToId))
        }.recover {
          case ex =>
            Logger.error("Could not translate incoming message due to exception", ex)
            BotResponse("I'm sorry - I couldn't translate your message!", Some(replyToId))
        }

      }
    }.getOrElse {
      Logger.error("Failed to parse incoming request to bot")
      Future.successful(BotResponse("I'm sorry - I'm don't seem to understand. Please try again later", None))
    }.map(response => Ok(Json.toJson(response)))

  }

  private def computeTranslationResult(text: String): Future[String] = {

    val cleanText = text.replaceAll(atUserRegex, "")

    translateClient.getTextLanguage(cleanText).flatMap { sourceLanguage =>
      val targetLanguage = translateClient.otherLanguage(sourceLanguage)
      translateClient.translateText(cleanText, sourceLanguage, targetLanguage).map { translationResult =>
        sourceLanguage match { //https://issues.scala-lang.org/browse/SI-6476 :(
          case Language.English => '"'+ cleanText + '"' + s" -> 「$translationResult」"
         case Language.Japanese => s"「$cleanText」-> " + '"' + translationResult + '"'
        }
      }
    }
  }

  private def getLocalizedTextForLanguage(key: String, language: Language.Value) = {
    val messages: Messages = MessagesImpl(Lang(language.toString), messagesApi)
    messages(key)
  }


}
