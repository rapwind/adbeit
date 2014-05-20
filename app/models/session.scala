package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Session(id: Pk[Long], u_id: Option[Long], fb_id: Option[Long], tw_id: Option[Long], hostname: String, permission:Int, create_date: Option[Date], expiration_date: Option[Date])

object Session {
  val simple = {
    get[Pk[Long]]("sessions.id") ~
    get[Option[Long]]("sessions.u_id") ~
    get[Option[Long]]("sessions.fb_id") ~
    get[Option[Long]]("sessions.tw_id") ~
    get[String]("sessions.hostname") ~
    get[Int]("sessions.permission") ~
    get[Option[Date]]("sessions.create_date") ~
    get[Option[Date]]("sessions.expiration_date") map {
      case id ~ u_id ~ fb_id ~ tw_id ~ hostname ~ permission ~ create_date ~ expiration_date => Session(id, u_id, fb_id, tw_id, hostname, permission, create_date, expiration_date)
    }
  }

  /**
   * Retrieve a Session from id.
   */
  def findById(id: Long): Option[Session] = {
    DB.withConnection { implicit connection =>
      SQL("select * from sessions where id = {id}").on(
        'id -> id
      ).as(Session.simple.singleOpt)
    }
  }

  /**
   * Retrieve all Session.
   */
  def findAll: Seq[Session] = {
    DB.withConnection { implicit connection =>
      SQL("select * from sessions").as(Session.simple *)
    }
  }

  /**
   * Create a Session.
   */
  def create(session: Session): Session = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into sessions (
            id, hostname, permission, create_date, expiration_date
          )
          values (
            {id}, {hostname}, {permission}, {create_date}, {expiration_date}
          )
        """
      ).on(
        'id -> session.id,
        'hostname -> session.hostname,
        'permission -> session.permission,
        'create_date -> session.create_date,
        'expiration_date -> session.expiration_date
      ).executeUpdate()

      session
    }
  }

}