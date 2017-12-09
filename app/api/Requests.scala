package api

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaReads

/*
{
  "topic": {
    "id": 208,
    "name": "IT Peeps",
    "suggestion": "IT Peeps",
    "lastPostedAt": "2014-12-10T09:00:29Z",
    "createdAt": "2014-06-10T02:32:29Z",
    "updatedAt": "2014-06-10T02:32:29Z"
  },
  "post": {
    "id": 333,
    "topicId": 208,
    "replyTo": null,
    "message": "Let's party like it's 1999!",
    "account": {
      "id": 100,
      "name": "jessica",
      "fullName": "Jessica Fitzherbert",
      "suggestion": "Jessica Fitzherbert",
      "imageUrl": "https://typetalk.com/accounts/100/profile_image.png?t=1403577149000",
      "createdAt": "2014-06-24T02:32:29Z",
      "updatedAt": "2014-06-24T02:32:29Z"
    },
    "mention": null,
    "attachments": [],
    "likes": [],
    "talks": [],
    "links": [],
    "createdAt": "2014-12-10T09:00:29Z",
    "updatedAt": "2014-12-10T09:00:29Z"
  }
}*/


/*We also get "space" inside the request, which is missing from the example
   "space":{
      "key":"8Dg5D15cEk",
      "name":"Personal",
      "enabled":true,
      "imageUrl":"https://apps.nulab-inc.com/spaces/8Dg5D15cEk/photo/large"
   },
 */
case class Account
(
  id: Int,
  name: String,
  fullName: String,
  suggestion: String,
  imageUrl: String,
  createdAt: String,
  updatedAt: String
)

// createdAt/updatedAt/etc. would ideally be parsed out into dates, but that would be more involved and we don't use them right now
case class Topic
(
  id: Int,
  name: String,
  //Omitting "suggestion" because it's only in the documentation - not the actual requests that are currently sent
  lastPostedAt: String,
  createdAt: String,
  updatedAt: String
)
case class Post
(
  id: Int,
  topicId: Int,
  //Omitting "replyTo" because I don't know what type it is when it's not pull
  message: String,
  account: Account,
  // Omitting "mention" ~ "links"
  createdAt: DateTime,
  updatedAt: DateTime
)

case class BotRequest
(
  topic: Topic,
  post: Post,
)

/*
{
    "message":  "Hello!",
    "replyTo": 100,
    "fileKeys": [ "abcdef", "ghijk" ],
    "talkIds": [ 200, 300 ]
}
 */

case class BotResponse
(
  message: String,
  replyTo: Int,
  fileKeys: Option[Seq[String]] = None,
  talkIds: Option[Seq[Int]] = None
)

object Requests {
  //https://stackoverflow.com/a/45440349/4243650
  implicit val readsDateTime = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss'Z'")
  implicit val writesBotResponse = Json.writes[BotResponse]
  implicit val readsAccount = Json.reads[Account]
  implicit val readsTopic = Json.reads[Topic]
  implicit val readsPost = Json.reads[Post]
  implicit val readsBotRequest = Json.reads[BotRequest]
}