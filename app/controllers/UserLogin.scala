package controllers

import models.Users
import models.Users.{User, userWrites}
import play.api.db.slick.DBAction
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, JsSuccess, Json, Reads}
import play.api.mvc._


/**
 * Created by infinitu on 15. 1. 26..
 */
object UserLogin extends Controller{
  
  val invalidatedForm = BadRequest("not allow type")
  val forbidden = Forbidden("no such user or password missmatch")
  val loginNessesery = Forbidden("login Nessesary")
  val LOGINED = "logined"
  
  case class LoginForm(email:String, password:String)
  
  implicit val loginFormReads:Reads[LoginForm] =(
      (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )(LoginForm.apply _)

  val login = DBAction{implicit req=>
    req.body.asJson.map(_.validate[LoginForm]).map{
      case form:JsSuccess[LoginForm]=>
        Users(form.get.email,form.get.password)(req.dbSession)
        .map{loginData=>
          Ok(Json.toJson(loginData))
            .withSession(LOGINED->Json.toJson(loginData).toString)
        }.getOrElse(forbidden)
      case _=>
        invalidatedForm
    }.getOrElse(invalidatedForm)
  }

  val logout = Action { req =>
    Ok("successfully").withNewSession
  }
  
  val me = Action{req=>
    loginedMe(req).map(u=>Ok(Json.toJson(u))).getOrElse(loginNessesery)
  }
  
  val loginUI = TODO


  def loginedMe(req:Request[AnyContent]):Option[User] = {
    req.session.get(LOGINED)
      .map(Json.parse)
      .map(_.validate[User])
      .flatMap{
      case user:JsSuccess[User]=>
        Some(user.get)
      case _=>
        None
      }
  }
  
  
}
