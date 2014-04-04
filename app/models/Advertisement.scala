package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Advertisement(active: Int, company_id: Option[Long], name: String, description: String, url: String, point: Int, create_date: Option[Date], modified_date: Option[Date], expiration_date: Option[Date])

object Advertisement {
  val simple = {
    get[Int]("advertisements.active") ~
    get[Option[Long]]("advertisements.company_id") ~
    get[String]("advertisements.name") ~
    get[String]("advertisements.description") ~
    get[String]("advertisements.url") ~
    get[Int]("advertisements.point") ~
    get[Option[Date]]("advertisements.create_date") ~
    get[Option[Date]]("advertisements.modified_date") ~
    get[Option[Date]]("advertisements.expiration_date") map {
      case active ~ company_id ~ name ~ description ~ url ~ point ~ create_date ~ modified_date ~ expiration_date => Advertisement(active, company_id, name, description, url, point, create_date, modified_date, expiration_date)
    }
  }

  /**
   * Retrieve a Advertisement from id.
   */
  def findById(id: Long): Option[Advertisement] = {
    DB.withConnection { implicit connection =>
      SQL("select * from advertisements where id = {id}").on(
        'id -> id
      ).as(Advertisement.simple.singleOpt)
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[Advertisement] = {
    DB.withConnection { implicit connection =>
      SQL("select * from advertisements").as(Advertisement.simple *)
    }
  }

  /**
   * Add a advertisement to the user.
   */
  def addRelation(u_id: Long, ad_id: Long) {
    DB.withConnection { implicit connection =>
      SQL("insert into users_advertisements values({u_id}, {ad_id})").on(
        'u_id -> u_id,
        'ad_id -> ad_id
      ).executeUpdate()
    }
  }

  /**
   * Create a Ticket.
   */
  def create(advertisement: Advertisement): Advertisement = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into advertisements (
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

