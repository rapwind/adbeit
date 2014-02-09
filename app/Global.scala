//import scala.concurrent.Future
import play.api._
//import play.api.mvc._
//import play.api.mvc.Results._

import models._
import anorm._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.debug("global setting")
    InitialData.insert()
  }
}

/**
 * Initial set of data to be imported
 * in the sample application.
 */
object InitialData {
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() = {

    if(User.findAll.isEmpty) {

      //store_active, user_name, user_email, user_password, user_gender, user_rank, user_exp, user_create_date, user_modified_date

      Seq(
        User(1, "Guillaume Bort", "guillaume@sample.com", "secret", 1, 1, 100, Some(date("2014-01-01")), Some(date("2012-01-01"))),
        User(1, "Maxime Dantec", "maxime@sample.com", "secret", 1, 1, 100, Some(date("2014-01-01")), Some(date("2012-01-01"))),
        User(1, "Sadek Drobi", "sadek@sample.com", "secret", 1, 1, 100, Some(date("2014-01-01")), Some(date("2012-01-01"))),
        User(1, "Erwan Loisant", "erwan@sample.com", "secret", 1, 1, 100, Some(date("2014-01-01")), Some(date("2012-01-01")))
      ).foreach(User.create)
      Logger.debug("create user")

    }


    if(Facebook.findAll.isEmpty) {

      //fb_id, fb_active, user_id, fb_accesstoken, fb_expiration_date

      Seq(
        Facebook(Id(1), 1, Some(2), "test-token1", Some(date("2014-05-01"))),
        Facebook(Id(2), 1, Some(1), "test-token2", Some(date("2014-05-01"))),
        Facebook(Id(3), 1, Some(3), "test-token3", Some(date("2014-05-01"))),
        Facebook(Id(4), 1, Some(4), "test-token4", Some(date("2014-05-01")))
      ).foreach(Facebook.create)
      Logger.debug("create facebook")

    }

    if(Twitter.findAll.isEmpty) {

      //fb_id, fb_active, user_id, fb_accesstoken, fb_expiration_date

      Seq(
        Twitter(Id(1), 1, Some(2), "test-token1", Some(date("2014-05-01"))),
        Twitter(Id(2), 1, Some(1), "test-token2", Some(date("2014-05-01"))),
        Twitter(Id(3), 1, Some(3), "test-token3", Some(date("2014-05-01"))),
        Twitter(Id(4), 1, Some(4), "test-token4", Some(date("2014-05-01")))
      ).foreach(Twitter.create)
      Logger.debug("create twitter")

    }

  }
}