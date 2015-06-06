package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import views._
import models._
import views.html.defaultpages.notFound

object Application extends Controller {

  // 'variables'

  val Home = Redirect(routes.Application.list(0, 2, ""))

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


  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Favorite.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  def create() = Action {
    Ok(html.createForm(favoriteForm, Favorite.options))
  }

  def save() = TODO

  def edit(id: Long) = Action {
    Favorite.findById(id).map { favorite =>
      Ok(html.editForm(id, favoriteForm.fill(favorite), Favorite.options))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = Action { implicit request =>
    favoriteForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors, Favorite.options)),
      favorite => {
        Favorite.update(id, favorite)
        Home.flashing("success" -> "Favorite %s has been updated".format(favorite.name))
      }
    )
  }

  def delete(id: Long) = Action {
    Favorite.delete(id)
    Home.flashing("Success" -> "favorite has been deleted!")
  }

}