package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import utils.CryptUtil
import java.sql.Timestamp

case class Company(active: Int, name: String, address: String, url: String, create_date: Option[Date], modified_date: Option[Date])

object Company {
  val simple = {
    get[Int]("companies.active") ~
    get[String]("companies.name") ~
    get[String]("companies.address") ~
    get[String]("companies.url") ~
    get[Option[Date]]("companies.create_date") ~
    get[Option[Date]]("companies.modified_date") map {
      case active ~ name ~ address ~ url ~ create_date ~ modified_date => Company(active, name, address, url, create_date, modified_date)
    }
  }

  /**
   * Retrieve a Company from id.
   */
  def findById(id: Long): Option[Company] = {
    DB.withConnection { implicit connection =>
      SQL("select * from companies where id = {id}").on(
        'id -> id
      ).as(Company.simple.singleOpt)
    }
  }

  /**
   * Retrieve all Company.
   */
  def findAll: Seq[Company] = {
    DB.withConnection { implicit connection =>
      SQL("select * from companies").as(Company.simple *)
    }
  }

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[Company] = {
    DB.withConnection { implicit connection =>
      SQL("select * from companies where address = {email}").on(
        'address -> email
      ).as(Company.simple.singleOpt)
    }
  }

  /**
   * Create a Company.
   */
  def create(company: Company): Company = {
    DB.withConnection { implicit c =>
      SQL(
        """
          insert into companies (
            active, name, address, url, create_date, modified_date
          )
          values (
            {active}, {name}, {address}, {url}, {create_date}, {modified_date}
          )
        """
      ).on(
        'active -> company.active,
        'name -> company.name,
        'address -> company.address,
        'url -> company.url,
        'create_date -> company.create_date,
        'modified_date -> company.modified_date
      ).executeUpdate()

      company
    }
  }

}