package clients

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
class GoogleTranslateClient @Inject()(config: Configuration)(implicit executionContext: ExecutionContext) {

  private val apiKey = config.get[String]("google-translate.key")
  //Using a deprecated method isn't ideal, but there's no easy way to construct a credentials object (for setCredentials())
  //with just an API key, so this was easier/cleaner
  private val translationService = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService

  def translateText(text: String, sourceLanguage: Language.Value, destinationLanguage: Language.Value): Future[String] = {
    Future.successful {
      val translation = translationService.translate(
        text,
        TranslateOption.sourceLanguage(sourceLanguage.toString),
        TranslateOption.targetLanguage(destinationLanguage.toString))
      translation.getTranslatedText
    }
  }

  def getTextLanguage(text: String): Future[Language.Value] = {
    Future.successful {
      val textLanguage = translationService.detect(text).getLanguage
      Language.values.find(_.toString == textLanguage).getOrElse {
        throw new RuntimeException(s"Text <$text> appears to be of unsupported language $textLanguage")
      }
    }
  }

  def otherLanguage(language: Language.Value): Language.Value = {
    //This approach only makes sense as long as we only support two languages
    Language.values.find(_ != language).getOrElse {
      throw new RuntimeException(s"Invalid language $language")
    }
  }

}
