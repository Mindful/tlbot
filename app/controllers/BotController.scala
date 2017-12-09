package controllers

import javax.inject._

import play.api.mvc._
import api._

import scala.concurrent.Future
import play.api.libs.json.{JsValue, Json}


@Singleton
class BotController @Inject()(cc: ControllerComponents) extends AbstractController(cc)  {

  import Requests._

  def receiveMessage = Action.async(parse.json) { request: Request[JsValue]  =>
    Json.fromJson[BotRequest](request.body).map { botRequest =>
      val response = BotResponse(s"Thanks for the message ${botRequest.post.account.fullName}", botRequest.post.id)
      Future.successful(Ok(Json.toJson(response)))
    }.getOrElse(Future.successful(BadRequest("Unable to parse bot request")))

  }

}
