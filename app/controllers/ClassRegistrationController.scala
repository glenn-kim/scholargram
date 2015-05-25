package controllers

import java.sql.Date

import controllers.ClassController._
import exception.{InvalidateParameterException, InvalidDataIntegraityException}
import models.{ClassRegistrations, Classes, Users}
import models.Users.{User, userWrites}
import play.api.db.slick.DBAction
import play.api.db.slick.DB
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.api.Play.current

/**
 * Created by infinitu on 15. 2. 2..
 */
object ClassRegistrationController extends Controller{

  val forbidden = Forbidden("does not have permission to request it.")
  val badrequest = Forbidden("wrong format.")

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

  case class AcceptForm(studentId:Int, acceptLevel:Int)
  implicit val acceptFormReads:Reads[AcceptForm] =
    ((JsPath \ "student_id").read[Int] and (JsPath \ "accept_level").read[Int])(AcceptForm.apply _)
  implicit val acceptFormListReads = Reads.seq(acceptFormReads)
  
  def acceptRegister(classId:Int) = Action{req=>
    UserLogin.loginedMe(req)
      .map{implicit user=>
          
        req.body.asJson.flatMap{
          case obj:JsObject=>
            obj.asOpt[AcceptForm].map(Seq(_))
          case arr:JsArray=>
            arr.asOpt[Seq[AcceptForm]]
          case _=>
            throw new InvalidateParameterException("it allow jsobject and jsarray")
        }
        .map{inp=>
          DB.withTransaction{ implicit session=>
            try{
              inp.foreach(ClassRegistrations.accept(classId,_))
              Ok("successfully")
            }catch {case t:Throwable=>
              session.rollback()
              throw t
            }
          }
        }.getOrElse(badrequest)
      }.getOrElse(UserLogin.loginNessesery)
  }
  
  case class JoinForm(identity:Option[String], major:Option[String])
  implicit val JoinFormReads:Reads[JoinForm] =
    ((JsPath \ "identity").readNullable[String] and (JsPath \ "major").readNullable[String])(JoinForm.apply _)
  
  def registerClass(classId:Int)=DBAction { req =>
    UserLogin.loginedMe(req)
      .map { implicit user =>
        implicit val session = req.dbSession
          user match {
            case User(id, name, "student") =>
              req.body.asJson.flatMap(_.asOpt[JoinForm])
                .map{ form=>
                  ClassRegistrations.register(classId,form)
                  Ok("Successfully")
                }
                .getOrElse(badrequest)
            case User(id, name, "professor") =>
            forbidden
          }
        }
      .getOrElse(UserLogin.loginNessesery)
  } 
}
