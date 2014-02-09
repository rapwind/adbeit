package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Likes(active: Int, user_id: Option[Long], ad_id: Option[Long], url: String, create_date: Option[Date])

object Likes {
  val simple = {
    get[Int]("likes.active") ~
    get[Option[Long]]("likes.user_id") ~
    get[Option[Long]]("likes.ad_id") ~
    get[String]("likes.url") ~
    get[Option[Date]]("likes.create_date") map {
      case active ~ user_id ~ ad_id ~ url ~ create_date => Likes(active, user_id, ad_id, url, create_date)
    }
  }

  /**
   * Retrieve all Likes.
   */
  def findAll: Seq[Likes] = {
    DB.withConnection { implicit connection =>
      SQL("select * from likes").as(Likes.simple *)
    }
  }

  /**
   * Create a Likes.
   */
  def create(likes: Likes): Likes = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into likes (
            active, user_id, ad_id, url, create_date
          )
          values (
            {active}, {user_id}, {ad_id}, {url}, {create_date}
          )
        """
      ).on(
        'active -> likes.active,
        'user_id -> likes.user_id,
        'url -> likes.url,
        'create_date -> likes.create_date
      ).executeUpdate()

      likes
    }
  }

}