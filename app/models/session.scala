package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Session(id: Pk[Long], uu_id: Option[Long], hostname: String, permission:Int, create_date: Option[Date], expiration_date: Option[Date])

object Session {
  val simple = {
    get[Pk[Long]]("sessions.id") ~
    get[Option[Long]]("sessions.uu_id") ~
    get[String]("sessions.hostname") ~
    get[Int]("sessions.permission") ~
    get[Option[Date]]("sessions.create_date") ~
    get[Option[Date]]("sessions.expiration_date") map {
      case id ~ uu_id ~ hostname ~ permission ~ create_date ~ expiration_date => Session(id, uu_id, hostname, permission, create_date, expiration_date)
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
            id, uu_id, hostname, permission, create_date, expiration_date
          )
          values (
            {id}, {uu_id}, {hostname}, {permission}, {create_date}, {expiration_date}
          )
        """
      ).on(
        'id -> session.id,
        'uu_id -> session.uu_id,
        'hostname -> session.hostname,
        'permission -> session.permission,
        'create_date -> session.create_date,
        'expiration_date -> session.expiration_date
      ).executeUpdate()

      session
    }
  }

}