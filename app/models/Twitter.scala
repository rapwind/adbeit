package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Twitter(id: Pk[Long], active: Int, user_id: Option[Long], accesstoken: String, expiration_date: Option[Date])

object Twitter {
  val simple = {
    get[Pk[Long]]("twitter.id") ~
    get[Int]("twitter.active") ~
    get[Option[Long]]("twitter.user_id") ~
    get[String]("twitter.accesstoken") ~
    get[Option[Date]]("twitter.expiration_date") map {
      case id ~ active ~ user_id ~ accesstoken ~ expiration_date => Twitter(id, active, user_id, accesstoken, expiration_date)
    }
  }

  /**
   * Retrieve all Twitter.
   */
  def findAll: Seq[Twitter] = {
    DB.withConnection { implicit connection =>
      SQL("select * from twitter").as(Twitter.simple *)
    }
  }

  /**
   * Create a Twitter.
   */
  def create(twitter: Twitter): Twitter = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into twitter (
            id, active, user_id, accesstoken, expiration_date
          )
          values (
            {id}, {active}, {user_id}, {accesstoken}, {expiration_date}
          )
        """
      ).on(
        'id -> twitter.id,
        'active -> twitter.active,
        'user_id -> twitter.user_id,
        'accesstoken -> twitter.accesstoken,
        'expiration_date -> twitter.expiration_date
      ).executeUpdate()

      twitter
    }
  }

}