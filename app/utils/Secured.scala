package utils

import play.api._
import play.api.mvc._

import controllers._

trait Secured {
  private def uuid(request: RequestHeader) = request.session.get("userInfo")

  // Facebook未認証時のリダイレクト
  private def onUnauthorizedFacebook(request: RequestHeader) = Results.Redirect(routes.FacebookOauth.signin)

  // Actionに認証をまかせてラップ(Facebook認証)
  def IsAuthenticatedFacebook(f: => String => Request[AnyContent] => Result) = Security.Authenticated(uuid, onUnauthorizedFacebook) { user =>
    Action(request => f(user)(request))
  }

}