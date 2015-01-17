package controllers
import play.api.db.slick.Config.driver.simple._
import play.api._
import play.api.mvc._
import models.Tables._

import scala.slick.lifted.TableQuery
;

object Application extends Controller {

  def index = Action {

    Ok(views.html.index("Your new application is ready."))
  }
  
}