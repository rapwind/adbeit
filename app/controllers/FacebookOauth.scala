package controllers


import play.api.Play
import play.api.mvc._
import play.api.libs.json._
import play.api.Logger
import play.api.libs.ws.{WS, Response}
import play.core.parsers._

import scala.concurrent.Future
import java.net.{URI, URLDecoder, URLEncoder}

import scala.util.matching.Regex

import concurrent._
import play.api.libs.concurrent.Execution.Implicits._
import ExecutionContext.Implicits.global


import anorm._

import utils._
import models._
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

  /**
   * Facebookからのコールバックを受ける関数
   */
  def callback = Action.async { implicit request =>
    request.queryString.get("code").flatMap(_.headOption).getOrElse("get_error") match {
      case "get_error" => errorCallback
      case _ => successCallback
    }
  }

  /**
   * コールバックが成功したとき
   * @param request
   */
  def successCallback(implicit request: RequestHeader) = {
    val code = request.queryString.get("code").flatMap(_.headOption).getOrElse("")
    requestAccessToken(code) flatMap { response =>


      val accessTokenList = parseAccessToken(response.body) match {
        case Some(tokenList) => tokenList
        case None => List("","")
      }
      val accessToken = accessTokenList.apply(0)
      val tokenExpiration = accessTokenList.apply(1)
      Logger.debug("accessToken=" + accessToken)
      Logger.debug("tokenExpiration=" + tokenExpiration)

      /*
      int HH = 359999 / 3600 → 99
      int mm = 359999 % 3600 / 60 → 59
      int ss = 359999 % 60 → 59
      */

      val DD = tokenExpiration.toLong / 86400
      val HH = tokenExpiration.toLong % 86400 / 3600
      val mm = tokenExpiration.toLong % 86400 % 3600 / 60
      val ss = tokenExpiration.toLong % 60
      Logger.debug("tokenExpiration=" + DD + ":" + HH + ":" + mm + ":" + ss)


      val userInfo = requestUserInfo(accessToken)
      userInfo map { response =>

        val json = response.json
        /**
         * 必要なデータの取り出し
         * 返り値  res : List[String]
         */
        val res = accessJs(json)
        res match {
          case Some(s) => {
            Logger.debug("res :" + s) //List(...)

            //既にFacebookモデルにuser_idが存在するか確認する。
            val user_id = checkFacebook(s, accessToken, tokenExpiration)
            user_id match {
              case Some(s) => {
                User.findById(s.toLong).map { user =>
                  // session部分を修正する必要あり
                  Redirect(routes.Projects.index).withSession("uuid" -> user.email)
                }.getOrElse(Forbidden)
              }
              case None => {
                Redirect(routes.SignUp.fbForm)
              }
            }

          }
          case None => {
            Logger.debug("Not exist.")
            Redirect(routes.Projects.index)
          }
        }

      } recover {
        case e: java.net.ConnectException => Ok("失敗")
      }

    } recover {
      case e: java.net.ConnectException => Ok("失敗")
    }
  }

  /**
   *jsonのデータをdbに保存する。
   * @param json
   */
  def accessJs(json: play.api.libs.json.JsValue): Option[List[String]] = {
    val pathName = List("id", "name", "email", "gender")
    var res: List[String] = List()

    for(word <- pathName) {
      val jsres = (json \ word).validate[String]
      jsres.fold(
        errors => {
          Logger.debug("errors :" + errors)  // 修正すべき箇所
          return None
        },
        s => {
          res = s :: res
          Logger.debug("res :" + s)
        }
      )
    }
    res = res.reverse
    Some(res)
  }

  /**
   *jsonのデータをdbに保存する。
   * @param res
   * @param token
   */
  def checkFacebook(res: List[String], token: String, expiration: String): Option[Long] = {

    Facebook.findById(res.apply(0).toLong).map { fb =>
      //facebookのidがある場合
      Logger.debug("createSession")
      Logger.debug("today :" + CryptUtil.getCurrentDate)
      Logger.debug("id: " + res.apply(0) + " name: " + res.apply(1))
      //facebook のaccesstokenを書き換える
      val exp = CryptUtil.calcExpiration(expiration)
      Logger.debug("expiration:" + exp)
      Facebook.updateToken(res.apply(0).toLong, token, Some(CryptUtil.date(exp)))

      fb.user_id match {
        case Some(s) => Some(s)
        case None => None
      }

    }.getOrElse(
      //facebookのidがない場合
      createFacebook(res, token, expiration)
    )
  }

  /**
   *jsonのデータをdbに保存する。
   * @param res
   * @param token
   */
  def createFacebook(res: List[String], token: String, expiration:String): Option[Long] = {

    val exp = CryptUtil.calcExpiration(expiration)

    val fb = Facebook(Id(res.apply(0).toLong), 1, None, token, Some(CryptUtil.date(exp)) )
    //Facebook(Id(1), 1, Some(2), "test-token1", Some(date("2014-05-01")))
    Facebook.create(fb)
    None
  }


  /**
   * コールバックが失敗したとき
   * @param request
   */
  def errorCallback(implicit request: RequestHeader) = scala.concurrent.Future {

    val error = request.queryString.get("error").flatMap(_.headOption).getOrElse("")
    val error_code = request.queryString.get("error_code").flatMap(_.headOption).getOrElse("")
    val error_description = request.queryString.get("error_description").flatMap(_.headOption).getOrElse("")
    val error_reason = request.queryString.get("error_reason").flatMap(_.headOption).getOrElse("")
    //Ok("error:" + error + " error_code:" + error_code + " error_description:" + error_description + " error_reason:" + error_reason)
    Redirect(routes.Projects.index)

  }

  /**
   * コールバックが成功あと、アクセストークンを取得する
   * Featureを作成する関数
   * @param code
   */
  def requestAccessToken(code: String) = WS.url("https://graph.facebook.com/oauth/access_token").post(Map(
    "client_id" -> Seq(clientId),
    "redirect_uri" -> Seq(redirectUri),
    "client_secret" -> Seq(clientSecret),
    "code" -> Seq(code)
    ))    // Future[Response]

  /**
   * user info 取得のFeatureを作成する関数
   * @param accessToken
   */
  def requestUserInfo(accessToken: String) = WS.url("https://graph.facebook.com/me" + "?access_token=" + accessToken).get()
  // Future[Response]

  /**
   * AccessTokenのパース
   * @param response
   */
  /*
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
  */

  /**
   * AccessTokenのパース
   * @param response
   */
  def parseAccessToken(response: String): Option[List[String]] = {
    // access_token取得用の正規表現
    var res: List[String] = List()
    val regex = new Regex("access_token=(.*)&expires=(.*)")
    response match {
      case regex(accessToken, expires) => {
        res = accessToken :: expires :: res
        Logger.debug("parse token: " + res)
        Some(res)
        //accessToken
      }
      case _ => {
        //"Access Token を取得できませんでした"
        None
      }
    }
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
