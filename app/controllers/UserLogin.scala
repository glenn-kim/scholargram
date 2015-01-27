package controllers

import models.Users
import models.Users.userwrites
import play.api.db.slick.DBAction
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, JsSuccess, Json, Reads}
import play.api.mvc._


/**
 * Created by infinitu on 15. 1. 26..
 */
object UserLogin extends Controller{
  
  val badreq = BadRequest("not allow type")
  val forbidden = Forbidden("no such user or password missmatch")
  val LOGINED = "logined"
  
  case class LoginForm(email:String, password:String)
  
  implicit val loginFormReads:Reads[LoginForm] =(
      (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )(LoginForm.apply _)

  def login() = DBAction{implicit req=>
    req.body.asJson.map(_.validate[LoginForm]).map{
      case form:JsSuccess[LoginForm]=>
        Users(form.get.email,form.get.password)(req.dbSession)
        .map{loginData=>
          Ok(Json.toJson(loginData))
            .withSession(LOGINED->loginData.id.toString)
        }.getOrElse(forbidden)
      case _=>
        badreq
    }.getOrElse(badreq)
  }
  
}
