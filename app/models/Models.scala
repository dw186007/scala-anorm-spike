package models

import anorm.{NotAssigned, Pk}
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

object Computer {

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

}