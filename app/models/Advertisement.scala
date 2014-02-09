package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Advertisement(active: Int, company_id: Option[Long], name: String, description: String, url: String, point: Int, create_date: Option[Date], modified_date: Option[Date], expiration_date: Option[Date])

case class UserAdvertisement(u_id: Option[Long], ad_id: Option[Long])


object Advertisement {
  val simple = {
    get[Int]("advertisement.active") ~
    get[Option[Long]]("advertisement.company_id") ~
    get[String]("advertisement.name") ~
    get[String]("advertisement.description") ~
    get[String]("advertisement.url") ~
    get[Int]("advertisement.point") ~
    get[Option[Date]]("advertisement.create_date") ~
    get[Option[Date]]("advertisement.modified_date") ~
    get[Option[Date]]("advertisement.expiration_date") map {
      case active ~ company_id ~ name ~ description ~ url ~ point ~ create_date ~ modified_date ~ expiration_date => Advertisement(active, company_id, name, description, url, point, create_date, modified_date, expiration_date)
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[Advertisement] = {
    DB.withConnection { implicit connection =>
      SQL("select * from advertisement").as(Advertisement.simple *)
    }
  }

  /**
   * Create a Ticket.
   */
  def create(advertisement: Advertisement): Advertisement = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into advertisement (
            active, company_id, name, description, url, point, create_date, modified_date, expiration_date
          )
          values (
            {active}, {company_id}, {name}, {description}, {url}, {point}, {create_date}, {modified_date}, {expiration_date}
          )
        """
      ).on(
        'active -> advertisement.active,
        'company_id -> advertisement.company_id,
        'name -> advertisement.name,
        'description -> advertisement.description,
        'url -> advertisement.url,
        'point -> advertisement.point,
        'create_date -> advertisement.create_date,
        'modified_date -> advertisement.modified_date,
        'expiration_date -> advertisement.expiration_date
      ).executeUpdate()

      advertisement
    }
  }

}


object UserAdvertisement {

  val simple = {
    get[Option[Long]]("user_advertisement.u_id") ~
    get[Option[Long]]("user_advertisement.ad_id") map {
      case u_id ~ ad_id => UserAdvertisement(u_id, ad_id)
    }
  }

  /**
   * Relate a ticket to the user team.
   */
  def relateAdvertisement(u_id: Int, ad_id: Int) {
    DB.withConnection { implicit connection =>
      SQL("insert into user_advertisement values({u_id}, {ad_id})").on(
        'u_id -> u_id,
        'ad_id -> ad_id
      ).executeUpdate()
    }
  }


}

