package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class Category(id : Pk[Long] = NotAssigned, name: String)
case class Favorite(id: Pk[Long] = NotAssigned, name: String, url: String, categoryId: Option[Long])

/**
 * Helper for pagination
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long){
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Favorite {

  // Anorm Parsers

  // This will parse a Favorite object from a ResultSet

  val simple = {
    get[Pk[Long]]("favorite.id") ~
    get[String]("favorite.name") ~
    get[String]("favorite.url") ~
    get[Option[Long]]("favorite.categoryId") map {
      case id~name~url~categoryId => Favorite(id, name, url, categoryId)
    }

  }

  val withCategory = Favorite.simple ~ (Category.simple ?) map {
    case favorite~company => (favorite,company)
  }


  // Queries

  def findById(id: Long): Option[Favorite] = {
    DB.withConnection { implicit connection =>
      SQL("Select * from favorite where id = {id}").on('id -> id).as(Favorite.simple.singleOpt)

    }
  }


}