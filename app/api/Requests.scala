package api

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaReads

/* Incoming webbook
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
  replyTo: Option[Int],
  message: String,
  account: Account,
  // Omitting "mention" ~ "links"
  createdAt: DateTime,
  updatedAt: DateTime
)

case class Message //Turns out we can use this for the incoming webhook and the result of a get message call
(
  topic: Topic,
  post: Post,
)


/* Response to get messages
{
   "mySpace":{
      "space":{
         "key":"8Dg5D15cEk",
         "name":"Personal",
         "enabled":true,
         "imageUrl":"https://apps.nulab-inc.com/spaces/8Dg5D15cEk/photo/large"
      },
      "myRole":null,
      "isPaymentAdmin":null,
      "invitableRoles":null,
      "myPlan":{
         "plan":{
            "key":"typetalk.free",
            "name":"Free Plan",
            "limitNumberOfUsers":10,
            "limitTotalAttachmentSize":1073741824
         },
         "enabled":true,
         "trial":null,
         "numberOfUsers":1,
         "totalAttachmentSize":0,
         "createdAt":"2017-12-09T05:26:36Z",
         "updatedAt":"2017-12-09T05:34:54Z"
      }
   },
   "team":null,
   "topic":{
      "id":59875,
      "name":"Testing Topic",
      "description":"",
      "suggestion":"Testing Topic",
      "isDirectMessage":false,
      "lastPostedAt":"2017-12-10T04:05:06Z",
      "createdAt":"2017-12-09T05:34:54Z",
      "updatedAt":"2017-12-09T05:34:54Z"
   },
   "post":{
      "id":13202988,
      "topicId":59875,
      "replyTo":null,
      "message":"@tlbot+ noreplyto",
      "account":{
         "id":55388,
         "name":"mindfuljt",
         "fullName":"Joshua",
         "suggestion":"Joshua",
         "imageUrl":"https://typetalk.com/accounts/55388/profile_image.png?t=1512797197364",
         "isBot":false,
         "createdAt":"2017-12-09T05:26:36Z",
         "updatedAt":"2017-12-10T03:59:58Z"
      },
      "mention":{
         "id":3198446,
         "readAt":null
      },
      "attachments":[

      ],
      "likes":[

      ],
      "talks":[

      ],
      "links":[

      ],
      "createdAt":"2017-12-10T04:03:46Z",
      "updatedAt":"2017-12-10T04:03:46Z"
   },
   "replies":[

   ],
   "exceedsAttachmentLimit":false
} */

/* Our outgoing response
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
  implicit val readsTypeTalkMessage = Json.reads[Message]
}