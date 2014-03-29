package controllers


import play.api.Play
import play.api.mvc._
import play.api.libs.json._
import play.api.Logger
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.libs.ws.{WS, Response}
import play.core.parsers._

import scala.concurrent.Future
import java.net.{URI, URLDecoder, URLEncoder}

import scala.util.matching.Regex

import concurrent._
import play.api.libs.concurrent.Execution.Implicits._
import ExecutionContext.Implicits.global


import utils._
import views._

object FacebookOauth extends Controller {

  /**
  * application.confからclient_idを取ってきてclientIDに格納
  */
  lazy val clientId = Play.current.configuration.getString("oauth.facebook.client_id").get

  /**
  * application.confからclient_secretを取ってきてclientSecretに格納
  */
  lazy val clientSecret = Play.current.configuration.getString("oauth.facebook.client_secret").get

  /**
  * application.confからredirect_uriを取ってきてredirectUriに格納
  */
  lazy val redirectUri = Play.current.configuration.getString("oauth.facebook.redirect_uri").get


  /**
  * Facebook未認証時にリダイレクトするように
  * IsAuthenticatedFacebookはActionに認証をまかせてラップさせている。
  */

  def signin = Action {
    Redirect("https://graph.facebook.com/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&scope=email,user_birthday,publish_stream")
  }

  def callback = Action.async { implicit request =>
    request.queryString.get("code").flatMap(_.headOption).getOrElse("get_error") match {
      case "get_error" => errorCallback
      case _ => successCallback
    }
  }

  def successCallback(implicit request: RequestHeader) = {
    val code = request.queryString.get("code").flatMap(_.headOption).getOrElse("")

    requestAccessToken(code) flatMap { response =>

      val accessToken = parseAccessToken(response.body)
      Logger.debug("postBody=" + accessToken)

      val userInfo = requestUserInfo(accessToken)

      userInfo map { response =>

        val json = response.json
        accessJs(json)

        Redirect(routes.Projects.index).withSession("uuid" -> "guillaume@sample.com")
      } recover {
        case e: java.net.ConnectException => Ok("失敗")
      }

    } recover {
      case e: java.net.ConnectException => Ok("失敗")
    }

    //Ok("success code :" + code )
  }

  def errorCallback(implicit request: RequestHeader) = scala.concurrent.Future {

    val error = request.queryString.get("error").flatMap(_.headOption).getOrElse("")
    val error_code = request.queryString.get("error_code").flatMap(_.headOption).getOrElse("")
    val error_description = request.queryString.get("error_description").flatMap(_.headOption).getOrElse("")
    val error_reason = request.queryString.get("error_reason").flatMap(_.headOption).getOrElse("")
    //Ok("error:" + error + " error_code:" + error_code + " error_description:" + error_description + " error_reason:" + error_reason)
    Redirect(routes.Projects.index)

  }

  def requestAccessToken(code: String) = WS.url("https://graph.facebook.com/oauth/access_token").post(Map(
    "client_id" -> Seq(clientId),
    "redirect_uri" -> Seq(redirectUri),
    "client_secret" -> Seq(clientSecret),
    "code" -> Seq(code)
    ))    // Future[Response]


  def requestUserInfo(accessToken: String) = WS.url("https://graph.facebook.com/me" + "?access_token=" + accessToken).get()
  // Future[Response]

  def parseAccessToken(response: String) = {
    // access_token取得用の正規表現
    val regex = new Regex("access_token=(.*)&expires=(.*)")
    response match {
      case regex(accessToken, expires) => {
        accessToken
      }
      case _ => {
        "Access Token を取得できませんでした"
      }
    }
  }

  def accessJs(json: play.api.libs.json.JsValue) = {
    val pathName = List("id", "name", "email", "gender")
    var res: List[String] = List()

    for(word <- pathName) {
      val jsres = (json \ word).validate[String]
      jsres.fold(
        errors => Logger.debug("errors :" + errors),
        s => {
          res = s :: res
          Logger.debug("res :" + s)
        }
      )
    }
    res = res.reverse
    Logger.debug("res :" + res)
    createFacebook(res)
  }

  def createFacebook(res: List[String]) = {
    Logger.debug("today :" + CryptUtil.getCurrentDate)
    Logger.debug("res :" + res.apply(0) + res.apply(1))
  }

}


/*
cmd + ctl + J : jsonの整形

{
  "id": "100002019632417",
  "name": "Hiroki Tanida",
  "first_name": "Hiroki",
  "last_name": "Tanida",
  "link": "https://www.facebook.com/HIROKI.TANIDA",
  "birthday": "09/24/1990",
  "hometown": {
    "id": "165080443539566",
    "name": "Maibara-shi, Shiga, Japan"
  },
  "location": {
    "id": "111937038818889",
    "name": "Kusatsu-shi, Shiga, Japan"
  },
  "education": [
    {
      "school": {
        "id": "435567356564987",
        "name": "米原高等学校"
      },
      "year": {
        "id": "136328419721520",
        "name": "2009"
      },
      "type": "High School"
    },
    {
      "school": {
        "id": "112169502142922",
        "name": "立命館大学(Ritsumeikan University)"
      },
      "year": {
        "id": "138879996141011",
        "name": "2013"
      },
      "type": "College"
    },
    {
      "school": {
        "id": "107604449269714",
        "name": "Ritsumeikan University"
      },
      "year": {
        "id": "120960561375312",
        "name": "2013"
      },
      "type": "College"
    },
    {
      "school": {
        "id": "137929602988310",
        "name": "立命館大学大学院"
      },
      "type": "Graduate School"
    }
  ],
  "gender": "male",
  "email": "kny.h-ss@ezweb.ne.jp",
  "timezone": 9,
  "locale": "ja_JP",
  "verified": true,
  "updated_time": "2013-12-24T07:15:57+0000",
  "username": "HIROKI.TANIDA"
}
*/
