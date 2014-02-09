package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Session(id: Pk[Long], user_id: Option[Long], hostname: String, create_date: Option[Date], expiration_date: Option[Date])

object Session {
  val simple = {
    get[Pk[Long]]("session.id") ~
    get[Option[Long]]("session.user_id") ~
    get[String]("session.hostname") ~
    get[Option[Date]]("session.create_date") ~
    get[Option[Date]]("session.expiration_date") map {
      case id ~ user_id ~ hostname ~ create_date ~ expiration_date => Session(id, user_id, hostname, create_date, expiration_date)
    }
  }

  /**
   * Retrieve all Session.
   */
  def findAll: Seq[Session] = {
    DB.withConnection { implicit connection =>
      SQL("select * from session").as(Session.simple *)
    }
  }

  /**
   * Create a Session.
   */
  def create(session: Session): Session = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into session (
            id, user_id, hostname, create_date, expiration_date
          )
          values (
            {id}, {user_id}, {hostname}, {create_date}, {expiration_date}
          )
        """
      ).on(
        'id -> session.id,
        'user_id -> session.user_id,
        'hostname -> session.hostname,
        'create_date -> session.create_date,
        'expiration_date -> session.expiration_date
      ).executeUpdate()

      session
    }
  }

}