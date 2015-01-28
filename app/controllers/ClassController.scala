package controllers

import exception.InvalidDataIntegraityException
import models.{ClassRegistrations, Classes, Users}
import models.Users.{User, userWrites}
import play.api.db.slick.DBAction
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._

/**
 * Created by infinitu on 15. 1. 27..
 */
object ClassController extends Controller{
  
  val forbidden = Forbidden("does not have permission to request it.")
  
  val classList = DBAction{req=>
    UserLogin.loginedMe(req)
      .map(Classes(_)(req.dbSession))
      .map(_.map(Json.toJson(_)))
      .map(JsArray(_))
      .map(Ok(_))
      .getOrElse(UserLogin.loginNessesery)
  }

  def registeredList(classId:Int) = DBAction{req=>
    UserLogin.loginedMe(req)
      .map {
      case User(id, name, "student") =>
        forbidden
      case User(id, name, "professor") =>
        val clsreg = ClassRegistrations(id,classId)(req.dbSession).map(Json.toJson(_))
        Ok(JsArray(clsreg))
      case _ =>
        throw new InvalidDataIntegraityException("user must be student or professor");
    }.getOrElse(UserLogin.loginNessesery)
  }
}
