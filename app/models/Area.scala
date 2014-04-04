package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Area(active: Int, name: String, create_date: Option[Date])

object Area {
  val simple = {
    get[Int]("areas.active") ~
    get[String]("areas.name") ~
    get[Option[Date]]("areas.create_date") map {
      case active ~ name ~ create_date => Area(active, name, create_date)
    }
  }

  /**
   * Retrieve a Area from id.
   */
  def findById(id: Int): Option[Area] = {
    DB.withConnection { implicit connection =>
      SQL("select * from areas where id = {id}").on(
        'id -> id
      ).as(Area.simple.singleOpt)
    }
  }

  /**
   * Retrieve all Area.
   */
  def findAll: Seq[Area] = {
    DB.withConnection { implicit connection =>
      SQL("select * from areas").as(Area.simple *)
    }
  }

  /**
   * Create a Area.
   */
  def create(area: Area): Area = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into areas (
            active, name, create_date
          )
          values (
            {active}, {name}, {create_date}
          )
        """
      ).on(
        'active -> area.active,
        'name -> area.name,
        'create_date -> area.create_date
      ).executeUpdate()

      area
    }
  }

}