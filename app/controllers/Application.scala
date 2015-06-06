package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import views._
import models._

object Application extends Controller {

  // 'variables'

  val Home = Redirect(Routes.Application.list(0, 2, ""))

  val favoriteForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText,
      "url" -> nonEmptyText,
      "category" -> optional(longNumber)
    )(Favorite.apply)(Favorite.unapply)
  )

  // Actions

  def index = Action { Home }

//
//  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
//    Ok(html.list)
//
//  }

  def create() = TODO

  def save() = TODO

  def edit(id: Long) = TODO

  def update(id: Long) = TODO

  def delete(id: Long) = TODO
}