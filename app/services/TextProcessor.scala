package services

import javax.inject.{Inject, Singleton}

import com.google.cloud.translate.Translate.TranslateOption

import scala.concurrent.{ExecutionContext, Future}
import com.google.cloud.translate._
import play.api.Configuration

object Language extends Enumeration {
  val English = Value("en")
  val Japanese = Value("ja")
}


@Singleton
class TextProcessor @Inject()(config: Configuration) (implicit executionContext: ExecutionContext) {

  private val apiKey = config.get[String]("google-translate.key")
  //Using a deprecated method isn't ideal, but there's no easy way to construct a credentials object (for setCredentials())
  //with just an API key, so this was easier/cleaner
  private val translationService = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService

  def translateText(text: String, from: Language.Value, to: Language.Value): Future[String] = {
    Future.successful {
      val translation = translationService.translate(
        text,
        TranslateOption.sourceLanguage(from.toString),
        TranslateOption.targetLanguage(to.toString))
      translation.getTranslatedText
    }
  }

}
