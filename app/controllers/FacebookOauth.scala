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
    Redirect("https://graph.facebook.com/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUri)
  }

  def callback(code: String) = Action.async {

    requestAccessToken(code) flatMap { response =>

      val accessToken = parseAccessToken(response.body)
      Logger.debug("postBody=" + accessToken)

      val userInfo = requestUserInfo(accessToken)

      userInfo map { response =>
        Logger.debug("postBody=" + response.json)
        Logger.debug("test future")
        Redirect(routes.Projects.index)
      } recover {
        case e: java.net.ConnectException => Ok("失敗")
      }

    } recover {
      case e: java.net.ConnectException => Ok("失敗")
    }

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


  /**
  * SignOutメソッド
  * sessionをflashingしてLoginメソッドにリダイレクト
  */
  def signout = Action {
    Redirect(routes.FacebookOauth.signin).withNewSession.flashing(
    "success" -> "You are now logged out"
    )
  }

}