package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import utils._
import models._
import views._

/**
 * Manage projects related operations.
 */
object Projects extends Controller with Secured {

  /**
   * Display the dashboard.
   */
  def index = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      Ok(
        html.dashboard(user)
      )
    }.getOrElse(Forbidden)
  }


}