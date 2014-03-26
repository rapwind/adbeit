package utils

import play.api._
import play.api.mvc._

import controllers._

/**
 * Provide security features
 */
trait Secured {
  private def uuid(request: RequestHeader) = request.session.get("uuid")

  /**
   * Retrieve the connected user email.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

  /**
   * Redirect to login if the user in not authorized.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(uuid, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /**
   * Check if the connected user is a member of this project.
   */
  /*
  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Project.isMember(project, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }
  */

  /**
   * Check if the connected user is a owner of this task.
   */
  /*
  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Task.isOwner(task, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }
  */

}
