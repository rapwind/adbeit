package controllers

import play.api.Play
import play.api.Play.current
import play.api.libs.oauth.{RequestToken, ServiceInfo, ConsumerKey, OAuth}
import play.api.mvc.{RequestHeader, Action, Controller}
import play.api.Play
import play.api.Logger

object TwitterOauth extends Controller {

  val cfg = Play.application.configuration
  val KEY = ConsumerKey(cfg.getString("twitter.consumerKey").get, cfg.getString("twitter.consumerSecret").get)

  val TWITTER = OAuth(ServiceInfo(
    "https://api.twitter.com/oauth/request_token",
    "https://api.twitter.com/oauth/access_token",
    "https://api.twitter.com/oauth/authorize", KEY),
    false)

  def authenticate = Action { request =>
    Logger.debug("request:" + request)
    request.queryString.get("denied").flatMap(_.headOption).map { denied =>

      Redirect(routes.Projects.index).withNewSession.flashing("success" -> "You've been logged out")

    }.getOrElse(
      request.queryString.get("oauth_verifier").flatMap(_.headOption).map { verifier =>
        val tokenPair = sessionTokenPair(request).get
        // We got the verifier; now get the access token, store it and back to index
        TWITTER.retrieveAccessToken(tokenPair, verifier) match {
          case Right(t) => {
            // We received the authorized tokens in the OAuth object - store it before we proceed
            Logger.debug("authorized token  :" + t.token)
            Logger.debug("authorized secret :" + t.secret)
            Redirect(routes.Projects.index).withSession("token" -> t.token, "secret" -> t.secret, "uuid" -> "guillaume@sample.com")
          }
          case Left(e) => throw e
        }
      }.getOrElse(
        TWITTER.retrieveRequestToken("http://localhost:9000/twsignup") match {
          case Right(t) => {
            // We received the unauthorized tokens in the OAuth object - store it before we proceed
            Logger.debug("unauthorized token  :" + t.token)
            Logger.debug("unauthorized secret :" + t.secret)
            Logger.debug("redirectUrl :" + TWITTER.redirectUrl(t.token))
            Redirect(TWITTER.redirectUrl(t.token)).withSession("token" -> t.token, "secret" -> t.secret)
          }
          case Left(e) => throw e
        })
    )
  }

  def sessionTokenPair(implicit request: RequestHeader): Option[RequestToken] = {
    for {
      token <- request.session.get("token")
      secret <- request.session.get("secret")
    } yield {
      RequestToken(token, secret)
    }
  }
}
