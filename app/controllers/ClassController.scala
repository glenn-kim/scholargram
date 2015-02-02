package controllers

import java.sql.Date

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
  val badrequest = Forbidden("wrong format.")
  
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
  
  
  case class CreateForm(className:String, startDate:Option[Date], endDate:Option[Date], schoolId:Int)
  implicit val createFormReads:Reads[CreateForm] =
    ((JsPath \ "class_name").read[String] and (JsPath \ "start_date").readNullable[Date]
      and (JsPath \ "end_date").readNullable[Date] and (JsPath \ "school_id").read[Int])(CreateForm.apply _)

  case class UpdateForm(className:Option[String], startDate:Option[Date], endDate:Option[Date], schoolId:Option[Int])
  implicit val updateFormReads:Reads[UpdateForm] =
    ((JsPath \ "class_name").readNullable[String] and (JsPath \ "start_date").readNullable[Date]
      and (JsPath \ "end_date").readNullable[Date] and (JsPath \ "school_id").readNullable[Int])(UpdateForm.apply _)


  val createClass = DBAction{req=>
    UserLogin.loginedMe(req)
      .map{implicit me=>
        implicit val session = req.dbSession
        req.body.asJson.flatMap(_.asOpt[CreateForm])
          .map(Classes.create)
          .map(x=>Ok{Json.toJson(x)})
          .getOrElse(badrequest)
    }.getOrElse(UserLogin.loginNessesery)
  }
  
  
  
  def updateClass(classId:Int) = DBAction{req=>
    UserLogin.loginedMe(req)
      .map{implicit me=>
      implicit val session = req.dbSession
      req.body.asJson.flatMap(_.asOpt[UpdateForm])
        .map(Classes.update(classId,_))
        .map(x=>Ok{Json.toJson(x)})
        .getOrElse(badrequest)
    }.getOrElse(UserLogin.loginNessesery)
  }
}
