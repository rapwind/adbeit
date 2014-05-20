package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Facebook(id: Pk[Long], active: Int, user_id: Option[Long], accesstoken: String, expiration_date: Option[Date])

object Facebook {
  val simple = {
    get[Pk[Long]]("facebook.id") ~
    get[Int]("facebook.active") ~
    get[Option[Long]]("facebook.user_id") ~
    get[String]("facebook.accesstoken") ~
    get[Option[Date]]("facebook.expiration_date") map {
      case id ~ active ~ user_id ~ accesstoken ~ expiration_date => Facebook(id, active, user_id, accesstoken, expiration_date)
    }
  }

  /**
   * Retrieve a Facebook from id.
   */
  def findById(id: Long): Option[Facebook] = {
    DB.withConnection { implicit connection =>
      SQL("select * from facebook where id = {id}").on(
        'id -> id
      ).as(Facebook.simple.singleOpt)
    }
  }

  /**
   * Retrieve all Facebook.
   */
  def findAll: Seq[Facebook] = {
    DB.withConnection { implicit connection =>
      SQL("select * from facebook").as(Facebook.simple *)
    }
  }

  /**
   * Update a facebook token.
   */
  def updateToken(id: Long, token: String) {
    DB.withConnection { implicit connection =>
      SQL("update facebook set accesstoken = {accesstoken} where id = {id}").on(
        'id -> id, 'accesstoken -> token
      ).executeUpdate()
    }
  }

  /**
   * Update a facebook user_id.
   */
  def updateUserId(id: Long, user_id: Long) {
    DB.withConnection { implicit connection =>
      SQL("update facebook set user_id = {user_id} where id = {id}").on(
        'id -> id, 'user_id -> user_id
      ).executeUpdate()
    }
  }

  /**
   * Create a Facebook.
   */
  def create(facebook: Facebook): Facebook = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into facebook (
            id, active, user_id, accesstoken, expiration_date
          )
          values (
            {id}, {active}, {user_id}, {accesstoken}, {expiration_date}
          )
        """
      ).on(
        'id -> facebook.id,
        'active -> facebook.active,
        'user_id -> facebook.user_id,
        'accesstoken -> facebook.accesstoken,
        'expiration_date -> facebook.expiration_date
      ).executeUpdate()

      facebook
    }
  }

}