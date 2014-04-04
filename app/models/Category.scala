package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Category(active: Int, name: String, create_date: Option[Date])

object Category {
  val simple = {
    get[Int]("categories.active") ~
    get[String]("categories.name") ~
    get[Option[Date]]("categories.create_date") map {
      case active ~ name ~ create_date => Category(active, name, create_date)
    }
  }

  /**
   * Retrieve a Category from id.
   */
  def findById(id: Int): Option[Category] = {
    DB.withConnection { implicit connection =>
      SQL("select * from categories where id = {id}").on(
        'id -> id
      ).as(Category.simple.singleOpt)
    }
  }

  /**
   * Retrieve all Category.
   */
  def findAll: Seq[Category] = {
    DB.withConnection { implicit connection =>
      SQL("select * from categories").as(Category.simple *)
    }
  }

  /**
   * Create a Category.
   */
  def create(category: Category): Category = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into categories (
            active, name, create_date
          )
          values (
            {active}, {name}, {create_date}
          )
        """
      ).on(
        'active -> category.active,
        'name -> category.name,
        'create_date -> category.create_date
      ).executeUpdate()

      category
    }
  }

}