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

      Seq(
        Area(1, "test-area1", Some(date("2014-05-01"))),
        Area(1, "test-area2", Some(date("2014-05-01"))),
        Area(1, "test-area3", Some(date("2014-05-01"))),
        Area(1, "test-area4", Some(date("2014-05-01")))
      ).foreach(Area.create)


      Seq(
        Category(1, "test-area1", Some(date("2014-05-01"))),
        Category(1, "test-area2", Some(date("2014-05-01"))),
        Category(1, "test-area3", Some(date("2014-05-01"))),
        Category(1, "test-area4", Some(date("2014-05-01")))
      ).foreach(Category.create)


      //store_active, user_name, user_email, user_password, user_gender, user_rank, user_exp, user_create_date, user_modified_date

      Seq(
        User(anorm.NotAssigned, 1, "Guillaume Bort", "guillaume@sample.com", "secret", 1, Some(1), Some(1), 1, 100, Some(date("2014-01-01")), Some(date("2012-01-01"))),
        User(anorm.NotAssigned, 1, "Maxime Dantec", "maxime@sample.com", "secret", 1, Some(3), Some(2), 1, 100, Some(date("2014-01-01")), Some(date("2012-01-01"))),
        User(anorm.NotAssigned, 1, "Sadek Drobi", "sadek@sample.com", "secret", 1, Some(2), Some(3), 1, 100, Some(date("2014-01-01")), Some(date("2012-01-01"))),
        User(anorm.NotAssigned, 1, "Erwan Loisant", "erwan@sample.com", "secret", 1, Some(4), Some(4), 1, 100, Some(date("2014-01-01")), Some(date("2012-01-01")))
      ).foreach(User.create)
      Logger.debug("create user")

    }

    /*
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
    */

    if(Company.findAll.isEmpty) {

      //fb_id, fb_active, user_id, fb_accesstoken, fb_expiration_date

      Seq(
        Company(1, "Cyber Agent", "cyber@test.com", "http://cyber/index", Some(date("2014-05-01")), Some(date("2014-05-01"))),
        Company(1, "Company2", "Company2@test.com", "http://Company2/index", Some(date("2014-05-01")), Some(date("2014-05-01"))),
        Company(1, "Company3", "Company3@test.com", "http://Company3/index", Some(date("2014-05-01")), Some(date("2014-05-01"))),
        Company(1, "Company4", "Company4@test.com", "http://Company4/index", Some(date("2014-05-01")), Some(date("2014-05-01")))
      ).foreach(Company.create)
      Logger.debug("create company")

    }

    if(Advertisement.findAll.isEmpty) {

      //fb_id, fb_active, user_id, fb_accesstoken, fb_expiration_date

      Seq(
        Advertisement(1, Some(1), "Ad1", "test advertisement 1", "http://advertisement/1", 10, Some(date("2014-05-01")), Some(date("2014-05-01")), Some(date("2014-05-01"))),
        Advertisement(1, Some(3), "Ad2", "test advertisement 2", "http://advertisement/2", 30, Some(date("2014-05-01")), Some(date("2014-05-01")), Some(date("2014-05-01"))),
        Advertisement(1, Some(4), "Ad3", "test advertisement 3", "http://advertisement/3", 20, Some(date("2014-05-01")), Some(date("2014-05-01")), Some(date("2014-05-01"))),
        Advertisement(1, Some(2), "Ad4", "test advertisement 4", "http://advertisement/4", 20, Some(date("2014-05-01")), Some(date("2014-05-01")), Some(date("2014-05-01")))
      ).foreach(Advertisement.create)
      Logger.debug("create advertisement")

      Seq(
        1 -> 2,
        2 -> 3,
        3 -> 1,
        4 -> 4
      ).foreach {
        case (u_id,ad_id) => Advertisement.addRelation(u_id, ad_id)
      }

    }

    if(Likes.findAll.isEmpty) {

      //fb_id, fb_active, user_id, fb_accesstoken, fb_expiration_date

      Seq(
        Likes(1, Some(2), Some(1), "http://likes/1", Some(date("2014-05-01"))),
        Likes(1, Some(1), Some(2), "http://likes/2", Some(date("2014-05-01"))),
        Likes(1, Some(3), Some(3), "http://likes/3", Some(date("2014-05-01"))),
        Likes(1, Some(4), Some(4), "http://likes/4", Some(date("2014-05-01")))
      ).foreach(Likes.create)
      Logger.debug("create twitter")

    }

  }
}