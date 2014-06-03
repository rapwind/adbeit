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

case class User(id: Pk[Long], active: Int, name: String, email: String, password: String, gender: Int, area_id: Option[Int], category_id: Option[Int], rank: Int, exp: Int, create_date: Option[Date], modified_date: Option[Date])

object User {
  val simple = {
    get[Pk[Long]]("users.id") ~
    get[Int]("users.active") ~
    get[String]("users.name") ~
    get[String]("users.email") ~
    get[String]("users.password") ~
    get[Int]("users.gender") ~
    get[Option[Int]]("users.area_id") ~
    get[Option[Int]]("users.category_id") ~
    get[Int]("users.rank") ~
    get[Int]("users.exp") ~
    get[Option[Date]]("users.create_date") ~
    get[Option[Date]]("users.modified_date") map {
      case id ~ active ~ name ~ email ~ password ~ gender ~ area_id ~ category_id ~ rank ~ exp ~ create_date ~ modified_date => User(id, active, name, email, password, gender, area_id, category_id, rank, exp, create_date, modified_date)
    }
  }

  /**
   * Retrieve a User from id.
   */
  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where id = {id}").on(
        'id -> id
      ).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve all user.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.simple *)
    }
  }

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where email = {email}").on(
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
         select * from users where
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
          insert into users (
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