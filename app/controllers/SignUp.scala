package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._


import anorm._

import utils._
import models._
import views._

object SignUp extends Controller {

  /**
   * Sign Up Form definition.
   *
   * Once defined it handle automatically, ,
   * validation, submission, errors, redisplaying, ...
   */
  val signupForm: Form[UserForm] = Form(

    // Define a mapping that will handle User values
    mapping(
      "username" -> text(minLength = 4),
      "email" -> nonEmptyText.verifying(
        "This email address is already registered.",
        email => User.findByEmail(email).isEmpty
      ),

      // Create a tuple mapping for the password/confirm
      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text
      ).verifying(
        // Add an additional constraint: both passwords must match
        "Passwords don't match", passwords => passwords._1 == passwords._2
      )

    )
    // The mapping signature doesn't match the User case class signature,
    // so we have to define custom binding/unbinding functions
    {
      // Binding: Create a User from the mapping result (ignore the second password and the accept field)
      (username, email, passwords) => UserForm(username, passwords._1, email)
    }
    {
      // Unbinding: Create the mapping values from an existing User value
      user => Some(user.username, user.email, (user.password, ""))
    }.verifying(
      // Add an additional constraint: The username must not be taken (you could do an SQL request here)
      "This username is not available",
      user => !Seq("admin", "guest").contains(user.username)
    )

  )

  /**
   * Display an empty form.
   */
  def form = Action {
    Ok(html.signup.form(signupForm));
  }

  /**
   * Display a form pre-filled with an existing User.
   */
  def fbForm = Action {
    val existingUser = UserForm(
      "fakeuser", "secret", "fake@gmail.com"
    )
    Ok(html.signup.form(signupForm.fill(existingUser)))
  }

  /**
   * Handle form submission.
   */
  def fbSubmit = Action { implicit request =>
    request.session.get("fbid").map { fbid =>

      Logger.debug("fbid: " + fbid)

      signupForm.bindFromRequest.fold(
        // Form has errors, redisplay it
        formWithErrors => BadRequest(html.signup.fbform(formWithErrors, fbid)),

        // We got a valid User value, display the summary
        form => {
          val user = User(anorm.NotAssigned, 1, form.username, form.email, form.password, 0, None, None, 0, 0, Some(CryptUtil.date(CryptUtil.getCurrentDate)), Some(CryptUtil.date(CryptUtil.getCurrentDate)) )
          User.create(user)

          User.findByEmail(form.email).map { user =>
            // session部分を修正する必要あり
            Facebook.updateUserId(fbid.toLong, user.id.get)

            Logger.debug("user: " + user)
            //User.create(user)
            Logger.debug("user: " + form)
            Redirect(routes.Projects.index).withSession("uuid" -> form.email)
            //Ok(html.signup.summary(form))
          }.getOrElse(Forbidden)

        }
      )

    }.getOrElse {
      Unauthorized("Oops, you are not connected")
    }
  }

  /**
   * Display a form pre-filled with an existing User.
   */
  def editForm = Action {
    val existingUser = UserForm(
      "fakeuser", "secret", "fake@gmail.com"
    )
    Ok(html.signup.form(signupForm.fill(existingUser)))
  }

  /**
   * Handle form submission.
   */
  def submit = Action { implicit request =>
    signupForm.bindFromRequest.fold(
      // Form has errors, redisplay it
      formWithErrors => BadRequest(html.signup.form(formWithErrors)),

      // We got a valid User value, display the summary
      form => {
        val user = User(anorm.NotAssigned, 1, form.username, form.email, form.password, 0, None, None, 0, 0, Some(CryptUtil.date(CryptUtil.getCurrentDate)), Some(CryptUtil.date(CryptUtil.getCurrentDate)) )
        User.create(user)
        Logger.debug("user: " + user)
        //User.create(user)
        Logger.debug("user: " + form)
        Ok(html.signup.summary(form))
      }
    )
  }

}