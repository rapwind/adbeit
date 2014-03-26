package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps
/*
postfix operator * should be enabled
[warn] by making the implicit value scala.language.postfixOps visible.
[warn] This can be achieved by adding the import clause 'import scala.language.postfixOps'
[warn] or by setting the compiler option -language:postfixOps.
[warn] See the Scala docs for value scala.language.postfixOps for a discussion
[warn] why the feature should be explicitly enabled.
*/

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class UserForm(
  username: String,
  password: String,
  email: String
)

case class User(active: Int, name: String, email: String, password: String, gender: Int, area_id: Option[Int], category_id: Option[Int], rank: Int, exp: Int, create_date: Option[Date], modified_date: Option[Date])

object User {
  val simple = {
    get[Int]("user.active") ~
    get[String]("user.name") ~
    get[String]("user.email") ~
    get[String]("user.password") ~
    get[Int]("user.gender") ~
    get[Option[Int]]("user.area_id") ~
    get[Option[Int]]("user.category_id") ~
    get[Int]("user.rank") ~
    get[Int]("user.exp") ~
    get[Option[Date]]("user.create_date") ~
    get[Option[Date]]("user.modified_date") map {
      case active ~ name ~ email ~ password ~ gender ~ area_id ~ category_id ~ rank ~ exp ~ create_date ~ modified_date => User(active, name, email, password, gender, area_id, category_id, rank, exp, create_date, modified_date)
    }
  }

  /**
   * Retrieve a User from id.
   */
  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where id = {id}").on(
        'id -> id
      ).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve all user.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
  }

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email
      ).as(User.simple.singleOpt)
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where
         email = {email} and password = {password}
        """
      ).on(
        'email -> email,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }


  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into user (
            active, name, email, password, gender, area_id, category_id, rank, exp, create_date, modified_date
          )
          values (
            {active}, {name}, {email}, {password}, {gender}, {area_id}, {category_id}, {rank}, {exp}, {create_date}, {modified_date}
          )
        """
      ).on(
        'active -> user.active,
        'name -> user.name,
        'email -> user.email,
        'password -> user.password,
        'gender -> user.gender,
        'area_id -> user.area_id,
        'category_id -> user.category_id,
        'rank -> user.rank,
        'exp -> user.exp,
        'create_date -> user.create_date,
        'modified_date -> user.modified_date
      ).executeUpdate()

      user
    }
  }
}