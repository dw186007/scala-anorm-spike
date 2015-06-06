package models

import java.util.{Date}

import com.sun.xml.internal.bind.v2.TODO
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
    case favorite~category => (favorite,category)
  }


  // Queries

  def findById(id: Long): Option[Favorite] = {
    DB.withConnection { implicit connection =>
      SQL("Select * from favorite where id = {id}").on('id -> id).as(Favorite.simple.singleOpt)

    }
  }

  /* Returns a page of Favorite/Category */

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Favorite, Option[Category])] = {
    val offset = pageSize * page

    DB.withConnection { implicit connection =>

      val favorites = SQL(
        """
          select * from favorite
          left join category on favorite.category_id = category.id
          where favorite.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
          'pageSize -> pageSize,
          'offset -> offset,
          'filter -> filter,
          'orderBy -> orderBy
        ).as(Favorite.withCategory *)

      val totalRows = SQL(
        """
          select count(*) from favorite
          left join category on favorite.category_id = category.id
          where favorite.name like {filter}
        """
      ).on(
          'filter -> filter
        ).as(scalar[Long].single)

      Page(favorites, page, offset, totalRows)

    }
  }

  def update(id: Long, favorite: Favorite) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update favorite
          set name = {name}, url = {url}, categoryId = {category_id}
          where id = {id}
        """
      ).on(
          'id -> id,
          'name -> favorite.name,
          'url -> favorite.url,
          'category_id -> favorite.categoryId
        ).executeUpdate()
    }
  }

  def insert(favorite: Favorite) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into favorite values (
            (select next value for favorite_seq),
            {name}, {url}, {categoryId}
          )
        """
      ).on(
          'name -> favorite.name,
          'url -> favorite.url,
          'categoryId -> favorite.categoryId
        ).executeUpdate()
    }
  }

  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from favorite where id = {id}").on('id -> id).executeUpdate()
    }
  }


}