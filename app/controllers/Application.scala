package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def list(p: Int, s: Int, f: String) = TODO

  def create() = TODO

  def save() = TODO

  def edit(id: Long) = TODO

  def update(id: Long) = TODO

  def delete(id: Long) = TODO
}