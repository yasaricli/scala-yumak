package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(email: String, name: String, password: String)

object User {

  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.email") ~
    get[String]("user.name") ~
    get[String]("user.password") map {
      case email ~ name ~ password => User(email, name, password)
    }
  }

  /**
   * Retrieve a User from email.
   */
  def getByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from yumak_user where email = {email}").on(
        'email -> email).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve all users.
   */
  def all: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from yumak_user where 
         email = {email} and password = {password}
        """).on(
          'email -> email,
          'password -> password).as(User.simple.singleOpt)
    }
  }

  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into yumak_user values (
            {email}, {name}, {password}
          )
        """).on(
          'email -> user.email,
          'name -> user.name,
          'password -> user.password).executeUpdate()

      user

    }
  }

}
